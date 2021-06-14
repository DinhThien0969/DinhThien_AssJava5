package edu.poly.ThienPCpolyshop.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import edu.poly.ThienPCpolyshop.config.StorageProperties;
import edu.poly.ThienPCpolyshop.exception.StorageException;
import edu.poly.ThienPCpolyshop.exception.StorageFileNotFoundException;
import edu.poly.ThienPCpolyshop.service.StorageService;

@Service
public class FileSystemStorageSericeImpl implements StorageService{
	private final Path rootLocation; //xác định đường dẫn gốc dùng để lưu hình ảnh
	
	@Override
	public String getStoredFilename(MultipartFile file, String id) { // Cho pép lưu thông tin và lấy thông tin về tên file được lưu trữ
		String ext = FilenameUtils.getExtension(file.getOriginalFilename());
		return "p" + id + "." + ext;
	}
	
	public FileSystemStorageSericeImpl(StorageProperties properties) {
		this.rootLocation = Paths.get(properties.getLocation());
	}
	
	@Override
	public void store(MultipartFile file, String storedFilename) {
		try {
			if(file.isEmpty()) {
				throw new StorageException("Không lưu trữ được tệp trống");
			}
			
			Path destinationFile = this.rootLocation.resolve(Paths.get(storedFilename))
							.normalize().toAbsolutePath();
			if(!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
				throw new StorageException("Không thể lưu trữ tệp bên ngoài thư mục");
			}
			try(InputStream inputStream = file.getInputStream()){
				Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (Exception e) {
			throw new StorageException("Không lưu trữ được tệp trống", e);
		}
	}
	
	@Override
	public Resource loadAsResource(String filename) {
		try {
			Path file = load(filename);
			Resource resource = new UrlResource(file.toUri());
			if(resource.exists() || resource.isReadable()) {
				return resource;
			}
			throw new StorageFileNotFoundException("Không thể đọc tệp: "+filename);
		} catch (Exception e) {
			throw new StorageFileNotFoundException("Không thể đọc tệp: "+filename);
		}
	}
	
	@Override
	public Path load(String filename) {
		return rootLocation.resolve(filename);
	}
	@Override
	public void delete(String storedFilename) throws IOException {
		Path destinationFile = rootLocation.resolve(Paths.get(storedFilename)).normalize().toAbsolutePath();
		
		Files.delete(destinationFile);
	}
	@Override
	public void init() {
		try {
			Files.createDirectories(rootLocation);
			System.out.println(rootLocation.toString());
		} catch (Exception e) {
			throw new StorageException("Không thể khởi chạy bộ nhớ", e);
		}
	}

}
