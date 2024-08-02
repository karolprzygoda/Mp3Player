package karol.przygoda.mp3player.data;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Objects;

public class DataBaseManager {
    public DataBaseManager() {
    }

    public void initDataBase() {
        Connection conn = null;
        Statement stmt = null;

        try {
            conn = DriverManager.getConnection("jdbc:sqlite:music_database.db");
            stmt = conn.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS songs (id INTEGER PRIMARY KEY,title TEXT,artist TEXT,song_path TEXT,cover_path TEXT);";
            stmt.execute(sql);
        } catch (SQLException var12) {
            System.err.println(var12.getMessage());
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }

                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException var11) {
                System.err.println(var11.getMessage());
            }

        }

    }

    public void addSong(String title, String artist, String songPath) {
        String coverPath = "";

        String sql = "INSERT INTO songs (title, artist, song_path, cover_path) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DriverManager.getConnection("jdbc:sqlite:music_database.db");
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, title);
            pstmt.setString(2, artist);
            pstmt.setString(3, songPath);
            pstmt.setString(4, coverPath);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
    }




    public void deleteSong(int id) {
        String sqlDelete = "DELETE FROM songs WHERE id = ?";
        String sqlUpdate = "UPDATE songs SET id = id - 1 WHERE id > ?";
        Connection conn = null;

        try {
            conn = DriverManager.getConnection("jdbc:sqlite:music_database.db");
            PreparedStatement pstmt = conn.prepareStatement(sqlDelete);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement(sqlUpdate);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException var14) {
            System.err.println(var14.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException var13) {
                System.err.println(var13.getMessage());
            }

        }

    }

    public void updateSongCover(int id, String path) {
        String sql = "UPDATE songs SET cover_path = ?  WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DriverManager.getConnection("jdbc:sqlite:music_database.db");
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, path);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException var15) {
            System.err.println(var15.getMessage());
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }

                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException var14) {
                System.err.println(var14.getMessage());
            }

        }

    }

    public ArrayList<Songs> getAllSongs() {
        ArrayList<Songs> songs = new ArrayList<>();
        String sql = "SELECT * FROM songs";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection("jdbc:sqlite:music_database.db");
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while(rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String artist = rs.getString("artist");
                String songPath = rs.getString("song_path");
                String coverPath = rs.getString("cover_path");
                songs.add(new Songs(id, title, artist, songPath, coverPath));
            }
        } catch (SQLException var19) {
            System.err.println(var19.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }

                if (stmt != null) {
                    stmt.close();
                }

                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException var18) {
                System.err.println(var18.getMessage());
            }

        }

        return songs;
    }
}
