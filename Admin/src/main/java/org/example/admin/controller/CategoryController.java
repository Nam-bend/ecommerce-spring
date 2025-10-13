    package org.example.admin.controller;

    import org.example.library.entity.Category;
    import org.example.library.service.CategoryService;
    import org.springframework.dao.DataIntegrityViolationException;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.web.servlet.mvc.support.RedirectAttributes;

    import java.util.List;

    @Controller
    public class CategoryController {
        private final CategoryService categoryService;
        public CategoryController(CategoryService categoryService) {
            this.categoryService = categoryService;
        }

        @GetMapping("/categories")
        public String categories(Model model) {
            model.addAttribute("title", "Categories");
            List<Category> categories = categoryService.findAll();
            model.addAttribute("categories", categories);
            model.addAttribute("size", categories.size());
            model.addAttribute("title", "Category");
            model.addAttribute("categoryNew", new Category());
            return "categories";
        }
        @PostMapping("/add-category")
        public String addCategory(
                @ModelAttribute("categoryNew") Category category,
                RedirectAttributes attributes) {
            try {
                categoryService.save(category);
                attributes.addFlashAttribute("success", "Thêm danh mục thành công!");

            } catch (IllegalArgumentException e) {
                // Catch validation errors từ service
                attributes.addFlashAttribute("failed", e.getMessage());
            } catch (DataIntegrityViolationException e) {
                e.printStackTrace();
                attributes.addFlashAttribute("failed", "Tên danh mục đã tồn tại!");
            } catch (Exception e) {
                e.printStackTrace();
                attributes.addFlashAttribute("failed", "Lỗi hệ thống, vui lòng thử lại!");
            }

            return "redirect:/categories";
        }
        @GetMapping("/update-category")
        public String update(
                @RequestParam("id") Long id,
                @RequestParam("name") String name,
                RedirectAttributes attributes) {
            try {
                Category existing = categoryService.findById(id);

                // Kiểm tra xem có thay đổi không
                if (existing.getName().trim().equals(name.trim())) {
                    attributes.addFlashAttribute("success", "Danh mục không có thay đổi!");
                    return "redirect:/categories";
                }

                Category category = new Category();
                category.setId(id);
                category.setName(name);

                categoryService.update(category);
                attributes.addFlashAttribute("success", "Cập nhật danh mục thành công!");

            } catch (IllegalArgumentException e) {
                attributes.addFlashAttribute("failed", e.getMessage());
            } catch (RuntimeException e) {
                attributes.addFlashAttribute("failed", e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                attributes.addFlashAttribute("failed", "Lỗi hệ thống!");
            }

            return "redirect:/categories";
        }

        @RequestMapping(value = "/delete-category", method = {RequestMethod.PUT, RequestMethod.GET})
        public String delete(Long id, RedirectAttributes attributes){
            try {
                categoryService.deleteById(id);
                attributes.addFlashAttribute("success", "Deleted successfully");
            }catch (Exception e){
                e.printStackTrace();
                attributes.addFlashAttribute("failed", "Failed to deleted");
            }
            return "redirect:/categories";
        }

        @RequestMapping(value = "/enable-category", method = {RequestMethod.PUT, RequestMethod.GET})
        public String enable(Long id, RedirectAttributes attributes){
            try {
                categoryService.enabledById(id);
                attributes.addFlashAttribute("success", "Enabled successfully");
            }catch (Exception e){
                e.printStackTrace();
                attributes.addFlashAttribute("failed", "Failed to enabled");
            }
            return "redirect:/categories";
        }



    }
