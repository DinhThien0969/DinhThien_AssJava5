package edu.poly.ThienPCpolyshop.controller.admin;

import java.util.List;
import java.util.Optional;import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import org.springframework.web.servlet.ModelAndView;

import edu.poly.ThienPCpolyshop.domain.Account;
import edu.poly.ThienPCpolyshop.domain.Category;
import edu.poly.ThienPCpolyshop.model.AccountDto;
import edu.poly.ThienPCpolyshop.model.CategoryDto;
import edu.poly.ThienPCpolyshop.service.AccountService;
import edu.poly.ThienPCpolyshop.service.CategoryService;

@Controller
@RequestMapping("admin/accounts")
public class AccountController { //Lớp có chức năng tiếp nhận các yêu cầu và thực hiện gọi các chức năng xử lý

	@Autowired
	AccountService accountService;

	@GetMapping("add")
	public String add(Model model) {// hiển thị form addoredit
		model.addAttribute("account", new AccountDto());
		return "admin/accounts/addOrEdit";

	}
	@PostMapping("saveOrUpdate")
	public ModelAndView saveOrUpdate(ModelMap model, @Valid @ModelAttribute("account") AccountDto dto, BindingResult result) {// lưu nội dung khi thêm mới

		if(result.hasErrors()) {
			
			return new ModelAndView("admin/accounts/addOrEdit");
		}
		
		Account entity = new Account();// +Tạo đối tượng Category

		BeanUtils.copyProperties(dto, entity);// +cooy từ dto sang entity

		accountService.save(entity);// +sau đó save entity vào cơ sở dữ liệu

		model.addAttribute("message", "Chúc mừng bạn đã Save thành công");

		return new ModelAndView("forward:/admin/accounts", model);
	}
	@RequestMapping("")
	public String list(ModelMap model) {// Hiển thị danh sách account

		List<Account> list = accountService.findAll();// trả về danh sách account có trong CSDL

		model.addAttribute("accounts", list);// thiết lập thuộc tính cho model

		return "admin/accounts/list";
	}

//	@GetMapping("edit/{categoryId}")//sửa thông tin
//	public ModelAndView edit(ModelMap model ,@PathVariable("categoryId") Long categoryId) {// chỉnh sửa addoredit
//
//		Optional<Category> opt =categoryservice.findById(categoryId);//lấy id
//		CategoryDto dto= new CategoryDto();
//		if(opt.isPresent()) {//kiểm tra nếu có giá trị trả về
//			
//			Category entity = opt.get();//lấy dữ liệu của account
//			
//			BeanUtils.copyProperties(entity, dto);//cooy từ entity sang dto ==> chuyển sang cho model để hiển thị
//			
//			dto.setIsEdit(true);//nếu mà ở chế độ update là true
//			
//			model.addAttribute("account",dto);//thiết lập giá trị thuộc tính account
//			
//			return new ModelAndView( "admin/accounts/addOrEdit",model);//hiển thị thông tin
//		}
//		model.addAttribute("message","Category không tồn tại");//trường hợp ngược lại,nếu không tồn tại sẽ thông báo
//		
//		return new ModelAndView("forward:/admin/accounts",model);
//		
//		
//	}
//
//	@GetMapping("delete/{categoryId}")
//	public ModelAndView delete(ModelMap model ,  @PathVariable("categoryId") Long categoryId) {// xóa một bản ghi trong account
//
//		categoryservice.deleteById(categoryId);
//		
//		model.addAttribute("message", "Xóa thành công");
//		return new ModelAndView("forward:/admin/accounts/search",model);
//	}
//

//

//
//	@GetMapping("search")
//	public String search( ModelMap model ,@RequestParam (name="name", required = false) String name ) {//tìm kiếm thông tin dựa vào name
//
//		
//		List<Category> list=null;
//		if(StringUtils.hasText(name)) {//kiểm tra nội dung truyền về có nội dung hay không
//			
//			list=categoryservice.findByNameContaining(name);
//		}else {
//			list=categoryservice.findAll();
//			
//		}
//		model.addAttribute("accounts",list);//thiết lập danh sách cho thuộc tính accounts
//		
//		return "admin/accounts/search";
//	}
//	
//	@GetMapping("searchpaginated")
//	public String search( ModelMap model ,@RequestParam (name="name", required = false) String name ,
//			@RequestParam("page") Optional<Integer> page,//trang hiện tại
//			@RequestParam("size") Optional<Integer> size) {//size là kích thước hiển thị trên 1 trang
//
//		int curentPage=page.orElse(1);//nếu người dùng không chọn giá trị thì giá trị ngầm định sẽ là trang 1
//		
//		int pageSize =size.orElse(5);//giá trị ngầm định là 5 phần tử trên 1 trang
//		
//		Pageable pageable = PageRequest.of(curentPage-1, pageSize,Sort.by("name"));//sắp xếp theeo trường dữ liệu name
//		
//		Page<Category> resultPage = null;
//		
//		if(StringUtils.hasText(name)) {//kiểm tra nội dung truyền về có nội dung hay không
//			
//			resultPage=categoryservice.findByNameContaining(name,pageable);
//			
//			model.addAttribute("name",name);
//		}else {
//			resultPage=categoryservice.findAll(pageable);//nếu không được truyền vào thì sẽ hiện cả 
//			
//		}
//		//tính toán số trang được hiển thị
//		int totalPages= resultPage.getTotalPages(); //trả về các trang đã được phân trang
//		
//		if(totalPages > 0) {
//			
//			int start = Math.max(1, curentPage-2);
//			int end = Math.min(curentPage + 2, totalPages);
//			
//			if(totalPages >5) {
//				
//				if(end == totalPages) start = end-5;
//				else if(start == 1) end =start +5;
//			}
//			List<Integer>pageNumbers=IntStream.range(start, end)   //xác định các trang được sinh ra từ start đến end
//					.boxed()
//					.collect(Collectors.toList());
//			
//			model.addAttribute("pageNumbers",pageNumbers);
//		}
//		
//		model.addAttribute("categoryPage",resultPage);//thiết lập danh sách cho thuộc tính accounts
//		
//		return "admin/accounts/searchpaginated";//trả về searchpaginated
//	}
}
