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
@RequestMapping("admin/products")
public class ProductController {

	@Autowired
	CategoryService categoryservice;
	
	@Autowired
	ProductService productService;
	
	@Autowired
	StorageService storageService;

	@ModelAttribute("categories")
	public List<CategoryDto> getCategories(){
		return categoryservice.findAll().stream().map(item->{
			CategoryDto dto = new CategoryDto();
			BeanUtils.copyProperties(item, dto);
			return dto;	
		}).collect(Collectors.toList());
	}
	
	@GetMapping("add")
	public String add(Model model) {// hi???n th??? form addoredit
		
		ProductDto dto = new ProductDto();
		dto.setIsEdit(false);
		model.addAttribute("product", dto);				
		return "admin/products/addOrEdit";

	}

	@GetMapping("edit/{productId}")//s???a th??ng tin
	public ModelAndView edit(ModelMap model ,@PathVariable("productId") Long productId) {// ch???nh s???a addoredit

		Optional<Product> opt = productService.findById(productId);//l???y id
		ProductDto dto= new ProductDto();
		if(opt.isPresent()) {//ki???m tra n???u c?? gi?? tr??? tr??? v???
			
			Product entity = opt.get();//l???y d??? li???u c???a category
			
			BeanUtils.copyProperties(entity, dto);//cooy t??? entity sang dto ==> chuy???n sang cho model ????? hi???n th???
			
			dto.setCategoryId(entity.getCategory().getCategoryId());
			dto.setIsEdit(true);//n???u m?? ??? ch??? ????? update l?? true
			
			model.addAttribute("product",dto);//thi???t l???p gi?? tr??? thu???c t??nh category
			
			return new ModelAndView( "admin/products/addOrEdit",model);//hi???n th??? th??ng tin
		}
		model.addAttribute("message","S???n ph???m kh??ng t???n t???i");//tr?????ng h???p ng?????c l???i,n???u kh??ng t???n t???i s??? th??ng b??o
		
		return new ModelAndView("forward:/admin/products",model);
		
		
	}

	@GetMapping("/images/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String filename){
		Resource file = storageService.loadAsResource(filename);
		
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
					"attachment; filename=\"" + file.getFilename() + "\"").body(file);
	}
	
	@GetMapping("delete/{productId}")
	public ModelAndView delete(ModelMap model ,  @PathVariable("productId") Long productId) throws IOException {// x??a m???t b???n ghi trong category

		Optional<Product> opt = productService.findById(productId);
		
		if(opt.isPresent()) {
			if(!StringUtils.isEmpty(opt.get().getImage())) {
				storageService.delete(opt.get().getImage());
			}
			productService.delete(opt.get());
			
			model.addAttribute("message",  "S???n ph???m ???? ???????c x??a");
		}else {
			model.addAttribute("message",  "S???n ph???m kh??ng th??? x??a");
		}	
		
		categoryservice.deleteById(productId);
		
		model.addAttribute("message", "X??a th??nh c??ng");
		return new ModelAndView("forward:/admin/products",model);
	}

	@PostMapping("saveOrUpdate")
	public ModelAndView saveOrUpdate(ModelMap model, 
			@Valid @ModelAttribute("product") ProductDto dto, BindingResult result) {// l??u n???i dung khi th??m m???i

		if(result.hasErrors()) {
			
			return new ModelAndView("admin/products/addOrEdit");
		}
		
		Product entity = new Product();// +T???o ?????i t?????ng Category
		BeanUtils.copyProperties(dto, entity);// +cooy t??? dto sang entity

		Category category = new Category();
		category.setCategoryId(dto.getCategoryId());
		entity.setCategory(category);
		
		if(!dto.getImageFile().isEmpty()) {
			UUID uuid = UUID.randomUUID();
			String uuString = uuid.toString();
			
			entity.setImage(storageService.getStoredFilename(dto.getImageFile(), uuString));
			storageService.store(dto.getImageFile(), entity.getImage());
		}
		
		productService.save(entity);// +sau ???? save entity v??o c?? s??? d??? li???u

		model.addAttribute("message", "Save th??nh c??ng");

		return new ModelAndView("forward:/admin/products", model);
	}

	@RequestMapping("")
	public String list(ModelMap model) {// Hi???n th??? danh s??ch category

		List<Product> list = productService.findAll();// tr??? v??? danh s??ch category c?? trong CSDL

		model.addAttribute("products", list);// thi???t l???p thu???c t??nh cho model

		return "admin/products/list";
	}

	@GetMapping("search")
	public String search( ModelMap model ,@RequestParam (name="name", required = false) String name ) {//t??m ki???m th??ng tin d???a v??o name

		
		List<Category> list=null;
		if(StringUtils.hasText(name)) {//ki???m tra n???i dung truy???n v??? c?? n???i dung hay kh??ng
			
			list=categoryservice.findByNameContaining(name);
		}else {
			list=categoryservice.findAll();
			
		}
		model.addAttribute("products",list);//thi???t l???p danh s??ch cho thu???c t??nh products
		
		return "admin/products/search";
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
