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

    // board ���̺��� ���ڵ� ����
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
            System.out.println("getListCount() ����: " + ex);
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

    // board ���̺��� ���ڵ� ��������
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
            System.out.println("getBoardList() ���� : " + ex);
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

    // member ���̺����� ������ id�� ����ڸ� ��������
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
            System.out.println("getBoardByNum() ���� : " + ex);
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

    // board ���̺��� ���ο� �� �����ϱ�
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
            pstmt.setInt(7, board.getHit()); // ������ ����
            pstmt.setString(8, board.getIp());
            pstmt.executeUpdate();
        } catch (Exception ex) {
            System.out.println("insertBoard() ���� : " + ex);
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

    // ���õ� ���� ��ȸ�� �����ϱ�
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
            System.out.println("updateHit() ����: " + ex);
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

    // ���õ� �� �� ���� ��������
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
            System.out.println("getBoardByNum() ���� : " + ex);
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

    // ���õ� �� ���� �����ϱ�
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
            System.out.println("updateBoard() ���� : " + ex);
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

    // ���õ� �� �����ϱ� 
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
            System.out.println("deleteBoard() ���� : " + ex);
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