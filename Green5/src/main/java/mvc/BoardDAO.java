package mvc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import dto.DBUtil;

public class BoardDAO {
    private static BoardDAO instance;

    private BoardDAO() {
    }

    public static BoardDAO getInstance() {
        if (instance == null)
            instance = new BoardDAO();
        return instance;
    }

    // board 테이블의 레코드 개수
    public int getListCount(String items, String text) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        int x = 0;

        String sql;

        if (items == null && text == null)
            sql = "select count(*) from board";
        else
            sql = "select count(*) from board where " + items + " like '%" + text + "%'";

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            if (rs.next())
                x = rs.getInt(1);
        } catch (Exception ex) {
            System.out.println("getListCount() 에러: " + ex);
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (Exception ex) {
                throw new RuntimeException(ex.getMessage());
            }
        }
        return x;
    }

    // board 테이블의 레코드 가져오기
    public ArrayList<BoardDTO> getBoardList(int page, int limit, String items, String text) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        int total_record = getListCount(items, text);
        int start = (page - 1) * limit;

        String sql;

        if (items == null && text == null)
            sql = "select * from board order by num desc";
        else
            sql = "select * from board where " + items + " like '%" + text + "%' order by num desc";

        ArrayList<BoardDTO> list = new ArrayList<BoardDTO>();

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            int index = 0;
            while (rs.next()) {
                if (index >= start && index < (start + limit)) {
                    BoardDTO board = new BoardDTO();
                    board.setNum(rs.getInt("num"));
                    board.setId(rs.getString("id"));
                    board.setName(rs.getString("name"));
                    board.setSubject(rs.getString("subject"));
                    board.setContent(rs.getString("content"));
                    board.setRegist_day(rs.getString("regist_day"));
                    board.setHit(rs.getInt("hit"));
                    board.setIp(rs.getString("ip"));
                    list.add(board);
                }
                if (index >= (start + limit)) {
                    break;
                }
                index++;
            }
            return list;
        } catch (Exception ex) {
            System.out.println("getBoardList() 에러 : " + ex);
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (Exception ex) {
                throw new RuntimeException(ex.getMessage());
            }
        }
        return null;
    }

    // member 테이블에서 인증된 id의 사용자명 가져오기
    public String getLoginNameById(String id) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String name = null;
        String sql = "select * from member where id=?";
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            rs = pstmt.executeQuery();

            if (rs.next())
                name = rs.getString("name");

            return name;
        } catch (Exception ex) {
            System.out.println("getBoardByNum() 에러 : " + ex);
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (Exception ex) {
                throw new RuntimeException(ex.getMessage());
            }
        }
        return null;
    }

    // board 테이블에 새로운 글 삽입하기
    public void insertBoard(BoardDTO board) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();

            String sql = "insert into board values(?,?,?,?,?,?,?,?)";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, board.getNum());
            pstmt.setString(2, board.getId());
            pstmt.setString(3, board.getName());
            pstmt.setString(4, board.getSubject());
            pstmt.setString(5, board.getContent());
            pstmt.setString(6, board.getRegist_day());
            pstmt.setInt(7, board.getHit()); // 수정된 라인
            pstmt.setString(8, board.getIp());
            pstmt.executeUpdate();
        } catch (Exception ex) {
            System.out.println("insertBoard() 에러 : " + ex);
        } finally {
            try {
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (Exception ex) {
                throw new RuntimeException(ex.getMessage());
            }
        }
    }

    // 선택된 글의 조회수 증가하기
    public void updateHit(int num) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "update board set hit = hit + 1 where num = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, num);
            pstmt.executeUpdate();
        } catch (Exception ex) {
            System.out.println("updateHit() 에러: " + ex);
        } finally {
            try {
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (Exception ex) {
                throw new RuntimeException(ex.getMessage());
            }
        }
    }

    // 선택된 글 상세 내용 가져오기
    public BoardDTO getBoardByNum(int num, int page) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        BoardDTO board = null;

        updateHit(num);
        String sql = "select * from board where num=?";

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, num);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                board = new BoardDTO();
                board.setNum(rs.getInt("num"));
                board.setId(rs.getString("id"));
                board.setName(rs.getString("name"));
                board.setSubject(rs.getString("subject"));
                board.setContent(rs.getString("content"));
                board.setRegist_day(rs.getString("regist_day"));
                board.setHit(rs.getInt("hit"));
                board.setIp(rs.getString("ip"));
            }
            return board;
        } catch (Exception ex) {
            System.out.println("getBoardByNum() 에러 : " + ex);
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (Exception ex) {
                throw new RuntimeException(ex.getMessage());
            }
        }
        return null;
    }

    // 선택된 글 내용 수정하기
    public void updateBoard(BoardDTO board) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            String sql = "update board set name=?, subject=?, content=? where num=?";

            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);

            conn.setAutoCommit(false);

            pstmt.setString(1, board.getName());
            pstmt.setString(2, board.getSubject());
            pstmt.setString(3, board.getContent());
            pstmt.setInt(4, board.getNum());

            pstmt.executeUpdate();
            conn.commit();
        } catch (Exception ex) {
            System.out.println("updateBoard() 에러 : " + ex);
        } finally {
            try {
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (Exception ex) {
                throw new RuntimeException(ex.getMessage());
            }
        }
    }

    // 선택된 글 삭제하기 
    public void deleteBoard(int num) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        String sql = "delete from board where num=?";

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, num);
            pstmt.executeUpdate();
        } catch (Exception ex) {
            System.out.println("deleteBoard() 에러 : " + ex);
        } finally {
            try {
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (Exception ex) {
                throw new RuntimeException(ex.getMessage());
            }
        }
    }
}
