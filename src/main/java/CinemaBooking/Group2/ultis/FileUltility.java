package CinemaBooking.Group2.ultis;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import org.springframework.web.multipart.MultipartFile;

public class FileUltility {
	public static String uploadFileImage(MultipartFile file ,String folderName) {
		try {
			String folderUpload = System.getProperty("user.dir")+"/"+folderName;
			String fileName = System.currentTimeMillis()+file.getOriginalFilename();
			String strpath = String.format("%s/%s", folderUpload,fileName);	
			byte[] data=file.getBytes();
			FileOutputStream fout = new FileOutputStream(strpath);
			BufferedOutputStream buf = new BufferedOutputStream(fout);
			buf.write(data);
			buf.flush();
			buf.close();
			return fileName;
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return "";
	}
	public static String getBase64EncodedImage(String folderName,String imageName) {
		String folderUpload=System.getProperty("user.dir")+"/"+folderName;
		try {
			FileInputStream fin = new FileInputStream(folderName+"/"+imageName);
			byte[] data = fin.readAllBytes();
			return Base64.getEncoder().encodeToString(data);
		}
		catch (FileNotFoundException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return "";
	}
	public static String deleteFile(String folderName,String fileName) {
		try {
			Path pathFile = Path.of(System.getProperty("user.dir")+"/"+folderName+"/"+fileName);
			Files.delete(pathFile);
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return "";
	}
}
