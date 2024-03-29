package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import db.DBClient;
import dto.MovieInfoDto;
import interfaces.IMovieService;

public class MovieInfoDao implements IMovieService {

	private DBClient dbClient;
	private Connection connection;
	private PreparedStatement preparedStatement;

	public MovieInfoDao() {
		dbClient = DBClient.getInstance("movieinfo");
		connection = dbClient.getConnection();
	}

	/**
	 * 영화이름 검색하기 SELECT MovieInfo
	 */
	@Override
	public Vector<MovieInfoDto> selectMovieTitle(String searchWord) {

		Vector<MovieInfoDto> selectMovieTitleDtos = new Vector<>();

		try {

			String selectMovieTitleQuery = "SELECT * FROM view_movieInfoALL WHERE REPLACE(movieTitle, ' ', '') LIKE '%' ? '%' ";
			preparedStatement = connection.prepareStatement(selectMovieTitleQuery);
			preparedStatement.setString(1, searchWord);
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {

				MovieInfoDto dto = new MovieInfoDto();

				dto.setMovieInfoNum(resultSet.getInt("movieinfoNum"));
				dto.setMovieTitle(resultSet.getString("movieTitle"));
				dto.setDirectorName(resultSet.getString("directorName"));
				dto.setReleaseYear(resultSet.getInt("releaseYear"));
				dto.setReleaseMonth(resultSet.getInt("releaseMonth"));
				dto.setMoviePlot(resultSet.getString("moviePlot"));
				dto.setTotalIncome(resultSet.getInt("totalIncome"));
				dto.setAudience(resultSet.getInt("audience"));
				dto.setRating(resultSet.getFloat("rating"));
				dto.setReview1(resultSet.getString("review1"));
				dto.setReview2(resultSet.getString("review2"));
				dto.setReview3(resultSet.getString("review3"));

				selectMovieTitleDtos.add(dto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return selectMovieTitleDtos;
	}

	/**
	 * 영화정보 전체 조회하기 SELECT MovieInfo
	 */
	@Override
	public Vector<MovieInfoDto> selectAllMovieInfo() {

		Vector<MovieInfoDto> movieDtos = new Vector<>();

		try {

			String empolyeesInfo = "SELECT * FROM view_movieInfoALL";
			preparedStatement = connection.prepareStatement(empolyeesInfo);
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {

				MovieInfoDto dto = new MovieInfoDto();

				dto.setMovieInfoNum(resultSet.getInt("movieinfoNum"));
				dto.setMovieTitle(resultSet.getString("movieTitle"));
				dto.setDirectorName(resultSet.getString("directorName"));
				dto.setReleaseYear(resultSet.getInt("releaseYear"));
				dto.setReleaseMonth(resultSet.getInt("releaseMonth"));
				dto.setMoviePlot(resultSet.getString("moviePlot"));
				dto.setTotalIncome(resultSet.getInt("totalIncome"));
				dto.setAudience(resultSet.getInt("audience"));
				dto.setRating(resultSet.getFloat("rating"));
				dto.setReview1(resultSet.getString("review1"));
				dto.setReview2(resultSet.getString("review2"));
				dto.setReview3(resultSet.getString("review3"));

				movieDtos.add(dto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return movieDtos;
	}

	/**
	 * 영화정보 중복검사 SELECT MovieInfo MovieInfo의 INSERT기능 수행하기전 중복을 검사한다.
	 */
	@Override
	public boolean selectMovieDoubleCheck(String movieTitle, String movieDirector) {

		// 중복 체크변수
		boolean doubleCheck = false;
		String movieinfoNumCheck = null;

		try {
			// 중복검사
			String selectCheckQuery = "SELECT * FROM movieinfo " 
					+ "WHERE REPLACE(movieTitle, ' ', '') LIKE '%' ? '%' "
					+ "AND REPLACE(directorName, ' ', '') LIKE '%' ? '%' ";
			preparedStatement = connection.prepareStatement(selectCheckQuery);
			preparedStatement.setString(1, movieTitle);
			preparedStatement.setString(2, movieDirector);
			ResultSet checkRs = preparedStatement.executeQuery();

			while (checkRs.next()) {
				movieinfoNumCheck = checkRs.getString("movieinfoNum");
			}

			// 중복이 아니라면 INSERT
			if (movieinfoNumCheck == null) {
				doubleCheck = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return doubleCheck;
	}

	/**
	 * 영화정보 추가하기 INSERT MovieInfo
	 */
	@Override
	public int insertMovieInfo(MovieInfoDto dto) {

		int result = -1;
		try {

			// INSERT
			// 테이블 - movieInfo / 영화이름, 감독
			String insertQuery = "INSERT INTO movieInfo(movieTitle, directorName) VALUES(?, ?)";
			preparedStatement = connection.prepareStatement(insertQuery);
			preparedStatement.setString(1, dto.getMovieTitle());
			preparedStatement.setString(2, dto.getDirectorName());
			result = preparedStatement.executeUpdate();

			// SELECT
			// movieinfoNum을 조회하기 위함.
			String selectQuery = "SELECT * FROM movieinfo "
					+ "WHERE REPLACE(movieTitle, ' ', '') LIKE '%' ? '%' "
					+ "AND REPLACE(directorName, ' ', '') LIKE '%' ? '%' ";
			preparedStatement = connection.prepareStatement(selectQuery);
			preparedStatement.setString(1, dto.getMovieTitle());
			preparedStatement.setString(2, dto.getDirectorName());
			ResultSet resultSet = preparedStatement.executeQuery();
			int movieinfoNum = 0;
			while (resultSet.next()) {
				movieinfoNum = resultSet.getInt("movieinfoNum");
			}

			// INSERT
			// 테이블 - movieReleaseInfo / movieinfoNum, 개봉연도, 개봉월
			insertQuery = "INSERT INTO movieReleaseInfo(movieinfoNum, releaseYear, releaseMonth) VALUES(?, ?, ?)";
			preparedStatement = connection.prepareStatement(insertQuery);
			preparedStatement.setInt(1, movieinfoNum);
			preparedStatement.setInt(2, dto.getReleaseYear());
			preparedStatement.setInt(3, dto.getReleaseMonth());
			result = preparedStatement.executeUpdate();

			// INSERT
			// 테이블 - movieplot / movieinfoNum, 줄거리
			insertQuery = "INSERT INTO moviePlot(movieinfoNum, moviePlot) VALUES(?, ?)";
			preparedStatement = connection.prepareStatement(insertQuery);
			preparedStatement.setInt(1, movieinfoNum);
			preparedStatement.setString(2, dto.getMoviePlot());
			result = preparedStatement.executeUpdate();

			// INSERT
			// 테이블 - moviereport / movieinfoNum, 매출액, 관객수, 평점, review1, review2, review3
			insertQuery = "INSERT INTO movieReport(movieinfoNum, totalIncome, audience, rating, review1, review2, review3) VALUES(?, ?, ?, ?, ?, ?, ?)";
			preparedStatement = connection.prepareStatement(insertQuery);
			preparedStatement.setInt(1, movieinfoNum);
			preparedStatement.setInt(2, dto.getTotalIncome());
			preparedStatement.setInt(3, dto.getAudience());
			preparedStatement.setFloat(4, dto.getRating());
			preparedStatement.setString(5, dto.getReview1());
			preparedStatement.setString(6, dto.getReview2());
			preparedStatement.setString(7, dto.getReview3());
			result = preparedStatement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 영화정보 수정하기 UPDATE MovieInfo
	 */
	@Override
	public int updateMovieInfo(int movieinfoNum, MovieInfoDto dto) {

		int result = -1;

		try {
			// UPDATE
			// 테이블 - movieInfo / 영화이름, 감독
			String updateQuery = "UPDATE movieinfo SET movieTitle = ? , directorName = ? WHERE movieinfoNum = ?";
			preparedStatement = connection.prepareStatement(updateQuery);
			preparedStatement.setString(1, dto.getMovieTitle());
			preparedStatement.setString(2, dto.getDirectorName());
			System.out.println(dto.getMovieTitle());
			preparedStatement.setInt(3, movieinfoNum);
			try {
				result = preparedStatement.executeUpdate();
			} catch(SQLException s) {
				return -1;
			}

			// UPDATE
			// 테이블 - movieReleaseInfo / movieinfoNum, 개봉연도, 개봉월
			updateQuery = "UPDATE movieReleaseInfo SET releaseYear = ? , releaseMonth = ? WHERE movieinfoNum = ? ";
			preparedStatement = connection.prepareStatement(updateQuery);
			preparedStatement.setInt(1, dto.getReleaseYear());
			preparedStatement.setInt(2, dto.getReleaseMonth());
			preparedStatement.setInt(3, movieinfoNum);
			result = preparedStatement.executeUpdate();
			System.out.println(result);

			// UPDATE
			// 테이블 - movieplot / movieinfoNum, 줄거리
			updateQuery = "UPDATE moviePlot SET moviePlot = ? WHERE movieinfoNum = ? ";
			preparedStatement = connection.prepareStatement(updateQuery);
			preparedStatement.setString(1, dto.getMoviePlot());
			preparedStatement.setInt(2, movieinfoNum);
			result = preparedStatement.executeUpdate();
			System.out.println(result);

			// UPDATE
			// 테이블 - moviereport / movieinfoNum, 매출액, 관객수, 평점, review1, review2, review3
			updateQuery = "UPDATE movieReport SET totalIncome = ? , audience = ? , rating = ? , review1 = ? , review2 = ? , review3 = ? WHERE movieinfoNum = ? ";
			preparedStatement = connection.prepareStatement(updateQuery);
			preparedStatement.setInt(1, dto.getTotalIncome());
			preparedStatement.setInt(2, dto.getAudience());
			preparedStatement.setFloat(3, dto.getRating());
			preparedStatement.setString(4, dto.getReview1());
			preparedStatement.setString(5, dto.getReview2());
			preparedStatement.setString(6, dto.getReview3());
			preparedStatement.setInt(7, movieinfoNum);
			result = preparedStatement.executeUpdate();
			System.out.println(result);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 영화정보 삭제하기 DELETE MovieInfo
	 */
	@Override
	public int deleteMovieInfo(int movieinfoNum) {

		int result = -1;

		try {

			// DELETE
			String deleteQuery = "DELETE FROM movieinfo WHERE movieinfoNum = ? ";
			preparedStatement = connection.prepareStatement(deleteQuery);
			preparedStatement.setInt(1, movieinfoNum);
			result = preparedStatement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
}
