package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import db.DBClient;
import dto.ActorInfoDto;
import interfaces.IActorService;

public class ActorDao implements IActorService {

	private DBClient dbClient;
	private Connection connection;
	private PreparedStatement preparedStatement;

	public ActorDao() {
		dbClient = DBClient.getInstance("movieinfo");
		connection = dbClient.getConnection();
	}

	@Override
	public Vector<ActorInfoDto> selectActorInfor(String searchWord) {

		Vector<ActorInfoDto> selectActorInfordto = new Vector<>();

		try {

			String selectActorInforQuery = "SELECT * FROM view_actorInfoAll WHERE REPLACE(personName, ' ', '') LIKE '%' ? '%' ";
			preparedStatement = connection.prepareStatement(selectActorInforQuery);
			preparedStatement.setString(1, searchWord);
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {

				ActorInfoDto dto = new ActorInfoDto();

				dto.setRepresentativeMovie(resultSet.getString("대표작품"));
				dto.setRepresentativeRole(resultSet.getString("대표역할"));
				dto.setActorName(resultSet.getString("personName"));
				dto.setBirthYear(resultSet.getString("birthYear"));
				dto.setGender(resultSet.getString("gender"));
				dto.setActorTall(resultSet.getString("height"));
				dto.setActorWeight(resultSet.getString("weight"));
				dto.setMarriagePartner(resultSet.getString("marriegePartner"));

				selectActorInfordto.add(dto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return selectActorInfordto;
	}

	@Override
	public Vector<ActorInfoDto> selectAllActorInfor() {

		Vector<ActorInfoDto> dto = new Vector<>();

		try {

			String actorAllInfo = "select * from view_actorInfoAll";
			preparedStatement = connection.prepareStatement(actorAllInfo);
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {

				ActorInfoDto dto1 = new ActorInfoDto();
				dto1.setActorNum(resultSet.getInt("actorNum"));
				dto1.setRepresentativeMovie(resultSet.getString("대표작품"));
				dto1.setRepresentativeRole(resultSet.getString("대표역할"));
				dto1.setActorName(resultSet.getString("personName"));
				dto1.setBirthYear(resultSet.getString("birthYear"));
				dto1.setGender(resultSet.getString("gender"));
				dto1.setActorTall(resultSet.getString("height"));
				dto1.setActorWeight(resultSet.getString("weight"));
				dto1.setMarriagePartner(resultSet.getString("marriegePartner"));
				dto1.setPersonNum(resultSet.getInt("personNum"));
				dto.add(dto1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return dto;
	}

	@Override
	public int insertActorInfo(ActorInfoDto dto) {
		
		String insertQuery = "insert into personInfo(personName, gender, birthYear, height, weight, marriegePartner) values(?,?,?,?,?,?)";
		int result = -1;
		
		try {
			// personInfo 에 먼저 등록
			preparedStatement = connection.prepareStatement(insertQuery);
			preparedStatement.setString(1, dto.getActorName());
			preparedStatement.setString(2, dto.getGender());
			preparedStatement.setString(3, dto.getBirthYear());
			preparedStatement.setString(4, dto.getActorTall());
			preparedStatement.setString(5, dto.getActorWeight());
			preparedStatement.setString(6, dto.getMarriagePartner());
			result = preparedStatement.executeUpdate();
			System.out.println(result);
			
			String selectPersonInforQuery = "SELECT * FROM personInfo "
					+ "WHERE REPLACE(personName, ' ', '') LIKE '%' ? '%' "
					+ "AND REPLACE(birthYear, ' ', '') LIKE '%' ? '%' ";
			preparedStatement = connection.prepareStatement(selectPersonInforQuery);
			preparedStatement.setString(1, dto.getActorName());
			preparedStatement.setString(2, dto.getBirthYear());
			ResultSet resultSet = preparedStatement.executeQuery();

			int personNum = 0;
			
			while (resultSet.next()) {
				ActorInfoDto actorInfoDto = new ActorInfoDto();
				actorInfoDto.setPersonNum(resultSet.getInt("personNum"));
				personNum = actorInfoDto.getPersonNum();
			}

			insertQuery = "insert into actorInfo(personNum, 대표작품, 대표역할) values(?,?,?) ";
			preparedStatement = connection.prepareStatement(insertQuery);
			preparedStatement.setInt(1, personNum);
			preparedStatement.setString(2, dto.getRepresentativeMovie());
			preparedStatement.setString(3, dto.getRepresentativeRole());
			result = preparedStatement.executeUpdate();
			System.out.println(result);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;

	}

	@Override
	public boolean selectActorDoubleCheck(String actorName, int birthYear) {

		// 중복 체크변수
		boolean doubleCheck = false;
		int actorInfoNumCheck = -1;
		try {
			// 중복검사
			String selectCheckQuery = "SELECT * FROM view_actorInfoAll WHERE REPLACE(personName, ' ', '') LIKE '%' ? '%' AND birthYear = ? ";
			preparedStatement = connection.prepareStatement(selectCheckQuery);
			preparedStatement.setString(1, actorName);
			preparedStatement.setInt(2, birthYear);
			ResultSet checkRs = preparedStatement.executeQuery();

			while (checkRs.next()) {
				actorInfoNumCheck = checkRs.getInt("actorNum");
			}
			
			if (actorInfoNumCheck == -1) {
				doubleCheck = true;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return doubleCheck;
	}

	@Override
	public int deleteActorInfo(int personNum) {

		int actorNum = -1;
		int result = -1;
		try {

			// SELECT
			// 선택한 영화가 몇번인지
			String selectQuery = "SELECT * FROM view_actorInfoAll WHERE personNum = ? ";
			preparedStatement = connection.prepareStatement(selectQuery);
			preparedStatement.setInt(1, personNum);

			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				actorNum = rs.getInt("actorNum");

			}

			// DELETE
			String deleteQuery = "DELETE FROM actorInfo WHERE actorNum = ? ";
			preparedStatement = connection.prepareStatement(deleteQuery);
			preparedStatement.setInt(1, actorNum);
			result = preparedStatement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public int updateActorInfo(int personNum, ActorInfoDto dto) {

		int result = -1;

		try {
			// UPDATE
			// 테이블 - personInfo / 이름, 주민등록성별, 키, 몸무게, 배우자
			String updateQuery = "UPDATE personInfo SET personName = ? , gender = ? , birthYear = ? ,  height = ? , weight = ?, marriegePartner = ?WHERE personNum = ?";
			preparedStatement = connection.prepareStatement(updateQuery);
			preparedStatement.setString(1, dto.getActorName());
			preparedStatement.setString(2, dto.getGender());
			preparedStatement.setString(3, dto.getBirthYear());
			preparedStatement.setString(4, dto.getActorTall());
			preparedStatement.setString(5, dto.getActorWeight());
			preparedStatement.setString(6, dto.getMarriagePartner());
			preparedStatement.setInt(7, personNum);
			try {
				result = preparedStatement.executeUpdate();
			} catch(SQLException s) {
				return -1;
			}

			// UPDATE
			// 테이블 - actorInfo / 대표작품, 대표역할
			updateQuery = "UPDATE actorInfo SET 대표작품 = ? , 대표역할 = ? WHERE personNum = ? ";
			preparedStatement = connection.prepareStatement(updateQuery);
			preparedStatement.setString(1, dto.getRepresentativeMovie());
			preparedStatement.setString(2, dto.getRepresentativeRole());
			preparedStatement.setInt(3, personNum);
			result = preparedStatement.executeUpdate();
			System.out.println(result);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

}
