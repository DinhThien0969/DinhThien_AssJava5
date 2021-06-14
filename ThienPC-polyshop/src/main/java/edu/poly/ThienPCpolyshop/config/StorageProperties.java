package edu.poly.ThienPCpolyshop.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties("storage")
@Data
public class StorageProperties {
	private String location; //Xác đinh vị trí dùng để lưu các file được upload lên Service
}
