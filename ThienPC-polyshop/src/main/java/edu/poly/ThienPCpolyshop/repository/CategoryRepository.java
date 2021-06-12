package edu.poly.ThienPCpolyshop.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.poly.ThienPCpolyshop.domain.Category;
//là file interface extends JpaRepository cho phép cung cấp phương thức thực hiện các thao tác trên entity category như thêm xóa sửa cập nhập tìm kiếm 
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
	
	List<Category>findByNameContaining(String name);//tìm kiếm theo tên
	
	Page<Category>findByNameContaining(String name,Pageable pageable);//phân trang
}
