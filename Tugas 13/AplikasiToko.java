import java.sql.*;
import java.util.Scanner;

public class AplikasiToko {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        int pilihan;
        do {
            System.out.println("\n+--------------------------------+");
            System.out.println("|        MENU TOKO RETAIL        |");
            System.out.println("+--------------------------------+");
            System.out.println("| 1. Tampil Semua Data           |");
            System.out.println("| 2. Tambah Data                 |");
            System.out.println("| 3. Cari Data                   |");
            System.out.println("| 4. Ubah Data                   |");
            System.out.println("| 5. Hapus Data                  |");
            System.out.println("| 0. Keluar                      |");
            System.out.println("+--------------------------------+");
            System.out.print("Pilihan : ");
            
            while (!scanner.hasNextInt()) {
                System.out.print("Input harus berupa angka! Pilihan : ");
                scanner.next();
            }
            pilihan = scanner.nextInt();
            scanner.nextLine(); // Clear buffer

            switch (pilihan) {
                case 1: tampilSemuaData(); break;
                case 2: tambahData(); break;
                case 3: cariData(); break;
                case 4: ubahData(); break;
                case 5: hapusData(); break;
                case 0: System.out.println("Keluar dari program. Terima kasih!"); break;
                default: System.out.println("Pilihan tidak tersedia!");
            }
        } while (pilihan != 0);
    }

    // ================= [ MENU 1: TAMPIL DATA ] =================
    public static void tampilSemuaData() {
        String query = "SELECT * FROM barang";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("\n+-----------------------------------------------------------------+");
            System.out.println("|                    DAFTAR BARANG TOKO RETAIL                    |");
            System.out.println("+-----+------------+-----------------------------------+-------+------+");
            System.out.printf("| %-3s | %-10s | %-33s | %-5s | %-4s |\n", "#", "Kode", "Nama Barang", "Harga", "Stok");
            System.out.println("+-----+------------+-----------------------------------+-------+------+");

            int no = 1;
            while (rs.next()) {
                System.out.printf("| %-3d | %-10s | %-33s | %-5d | %-4d |\n",
                        no++,
                        rs.getString("kode"),
                        rs.getString("nama_barang"),
                        rs.getInt("harga"),
                        rs.getInt("stok"));
            }
            System.out.println("+-----+------------+-----------------------------------+-------+------+");
            System.out.println("Total: " + (no - 1) + " barang");

        } catch (SQLException e) {
            System.out.println("Error tampil data: " + e.getMessage());
        }
    }

    // ================= [ MENU 2: TAMBAH DATA ] =================
    public static void tambahData() {
        System.out.print("Masukkan Kode Barang : "); String kode = scanner.nextLine();
        System.out.print("Masukkan Nama Barang : "); String nama = scanner.nextLine();
        System.out.print("Masukkan Harga       : "); int harga = scanner.nextInt();
        System.out.print("Masukkan Stok        : "); int stok = scanner.nextInt();

        String query = "INSERT INTO barang (kode, nama_barang, harga, stok) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, kode);
            pstmt.setString(2, nama);
            pstmt.setInt(3, harga);
            pstmt.setInt(4, stok);
            
            if (pstmt.executeUpdate() > 0) {
                System.out.println("Data barang berhasil ditambahkan!");
            }
        } catch (SQLException e) {
            System.out.println("Gagal tambah data (Kode mungkin sudah ada): " + e.getMessage());
        }
    }

    // ================= [ MENU 3: CARI DATA ] =================
    public static void cariData() {
        System.out.print("Masukkan Nama Barang yang dicari: ");
        String keyword = scanner.nextLine();

        String query = "SELECT * FROM barang WHERE nama_barang LIKE ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n--- Hasil Pencarian ---");
            int no = 1;
            while (rs.next()) {
                System.out.printf("%d. [%s] %s - Harga: %d (Stok: %d)\n", 
                    no++, rs.getString("kode"), rs.getString("nama_barang"), rs.getInt("harga"), rs.getInt("stok"));
            }
            if (no == 1) System.out.println("Barang tidak ditemukan!");
        } catch (SQLException e) {
            System.out.println("Error cari data: " + e.getMessage());
        }
    }

    // ================= [ MENU 4: UBAH DATA ] =================
    public static void ubahData() {
        System.out.print("Masukkan Kode Barang yang ingin diubah: ");
        String kode = scanner.nextLine();

        System.out.print("Nama Barang Baru : "); String nama = scanner.nextLine();
        System.out.print("Harga Baru       : "); int harga = scanner.nextInt();
        System.out.print("Stok Baru        : "); int stok = scanner.nextInt();

        String query = "UPDATE barang SET nama_barang = ?, harga = ?, stok = ? WHERE kode = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, nama);
            pstmt.setInt(2, harga);
            pstmt.setInt(3, stok);
            pstmt.setString(4, kode);
            
            if (pstmt.executeUpdate() > 0) {
                System.out.println("Data barang dengan kode " + kode + " berhasil diperbarui!");
            } else {
                System.out.println("Kode barang tidak ditemukan!");
            }
        } catch (SQLException e) {
            System.out.println("Error ubah data: " + e.getMessage());
        }
    }

    // ================= [ MENU 5: HAPUS DATA ] =================
    public static void hapusData() {
        System.out.print("Masukkan Kode Barang yang ingin dihapus: ");
        String kode = scanner.nextLine();

        String query = "DELETE FROM barang WHERE kode = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, kode);
            if (pstmt.executeUpdate() > 0) {
                System.out.println("Barang dengan kode " + kode + " berhasil dihapus!");
            } else {
                System.out.println("Kode barang tidak ditemukan!");
            }
        } catch (SQLException e) {
            System.out.println("Error hapus data: " + e.getMessage());
        }
    }
}