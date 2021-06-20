package edu.poly.ThienPCpolyshop.controller.admin;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import edu.poly.ThienPCpolyshop.domain.Category;
import edu.poly.ThienPCpolyshop.domain.Product;
import edu.poly.ThienPCpolyshop.model.CategoryDto;
import edu.poly.ThienPCpolyshop.model.ProductDto;
import edu.poly.ThienPCpolyshop.service.CategoryService;
import edu.poly.ThienPCpolyshop.service.ProductService;
import edu.poly.ThienPCpolyshop.service.StorageService;

@Controller
@RequestMapping("admin/home")
public class HomeController {

	@Autowired
	CategoryService categoryservice;
	
	@Autowired
	ProductService productService;
	
	@Autowired
	StorageService storageService;

	
	@RequestMapping("")
	public String list(ModelMap model) {// Hiển thị danh sách category

		List<Product> list = productService.findAll();// trả về danh sách category có trong CSDL

		model.addAttribute("products", list);// thiết lập thuộc tính cho model

		return "admin/products/home";
	}

	
	
	@GetMapping("searchpaginated")
	public String search( ModelMap model ,@RequestParam (name="name", required = false) String name ,
			@RequestParam("page") Optional<Integer> page,//trang hiện tại
			@RequestParam("size") Optional<Integer> size) {//size là kích thước hiển thị trên 1 trang

		int curentPage=page.orElse(1);//nếu người dùng không chọn giá trị thì giá trị ngầm định sẽ là trang 1
		
		int pageSize =size.orElse(5);//giá trị ngầm định là 5 phần tử trên 1 trang
		
		Pageable pageable = PageRequest.of(curentPage-1, pageSize,Sort.by("name"));//sắp xếp theeo trường dữ liệu name
		
		Page<Category> resultPage = null;
		
		if(StringUtils.hasText(name)) {//kiểm tra nội dung truyền về có nội dung hay không
			
			resultPage=categoryservice.findByNameContaining(name,pageable);
			
			model.addAttribute("name",name);
		}else {
			resultPage=categoryservice.findAll(pageable);//nếu không được truyền vào thì sẽ hiện cả 
			
		}
		//tính toán số trang được hiển thị
		int totalPages= resultPage.getTotalPages(); //trả về các trang đã được phân trang
		
		if(totalPages > 0) {
			
			int start = Math.max(1, curentPage-2);
			int end = Math.min(curentPage + 2, totalPages);
			
			if(totalPages >5) {
				
				if(end == totalPages) start = end-5;
				else if(start == 1) end =start +5;
			}
			List<Integer>pageNumbers=IntStream.range(start, end)   //xác định các trang được sinh ra từ start đến end
					.boxed()
					.collect(Collectors.toList());
			
			model.addAttribute("pageNumbers",pageNumbers);
		}
		
		model.addAttribute("categoryPage",resultPage);//thiết lập danh sách cho thuộc tính products
		
		return "admin/products/searchpaginated";//trả về searchpaginated
	}
}
