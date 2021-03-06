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
	public String list(ModelMap model) {// Hi???n th??? danh s??ch category

		List<Product> list = productService.findAll();// tr??? v??? danh s??ch category c?? trong CSDL

		model.addAttribute("products", list);// thi???t l???p thu???c t??nh cho model

		return "admin/products/home";
	}

	
	
	@GetMapping("searchpaginated")
	public String search( ModelMap model ,@RequestParam (name="name", required = false) String name ,
			@RequestParam("page") Optional<Integer> page,//trang hi???n t???i
			@RequestParam("size") Optional<Integer> size) {//size l?? k??ch th?????c hi???n th??? tr??n 1 trang

		int curentPage=page.orElse(1);//n???u ng?????i d??ng kh??ng ch???n gi?? tr??? th?? gi?? tr??? ng???m ?????nh s??? l?? trang 1
		
		int pageSize =size.orElse(5);//gi?? tr??? ng???m ?????nh l?? 5 ph???n t??? tr??n 1 trang
		
		Pageable pageable = PageRequest.of(curentPage-1, pageSize,Sort.by("name"));//s???p x???p theeo tr?????ng d??? li???u name
		
		Page<Category> resultPage = null;
		
		if(StringUtils.hasText(name)) {//ki???m tra n???i dung truy???n v??? c?? n???i dung hay kh??ng
			
			resultPage=categoryservice.findByNameContaining(name,pageable);
			
			model.addAttribute("name",name);
		}else {
			resultPage=categoryservice.findAll(pageable);//n???u kh??ng ???????c truy???n v??o th?? s??? hi???n c??? 
			
		}
		//t??nh to??n s??? trang ???????c hi???n th???
		int totalPages= resultPage.getTotalPages(); //tr??? v??? c??c trang ???? ???????c ph??n trang
		
		if(totalPages > 0) {
			
			int start = Math.max(1, curentPage-2);
			int end = Math.min(curentPage + 2, totalPages);
			
			if(totalPages >5) {
				
				if(end == totalPages) start = end-5;
				else if(start == 1) end =start +5;
			}
			List<Integer>pageNumbers=IntStream.range(start, end)   //x??c ?????nh c??c trang ???????c sinh ra t??? start ?????n end
					.boxed()
					.collect(Collectors.toList());
			
			model.addAttribute("pageNumbers",pageNumbers);
		}
		
		model.addAttribute("categoryPage",resultPage);//thi???t l???p danh s??ch cho thu???c t??nh products
		
		return "admin/products/searchpaginated";//tr??? v??? searchpaginated
	}
}
