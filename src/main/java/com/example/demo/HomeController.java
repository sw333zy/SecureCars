package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    private UserService userService;
    @GetMapping ("/register")
    public String showRegistrationPage(Model model){
        model.addAttribute("user", new User());
        return "registration";
    }

    @PostMapping ("/register")
    public String processRegistrationPage(@Valid
                                           @ModelAttribute("user") User user,
                                           BindingResult result,
                                           Model model){
        model.addAttribute("user", user);
        if (result.hasErrors())
        {
            return "registration";
        }
        else
        {
            userService.saveUser(user);
            model.addAttribute("message", "User Account Created");
        }
        return "index";
    }


    @RequestMapping("/")
    public String index(){
        return "index";
    }
    @RequestMapping("/login")
    public String login(){
        return "login";
    }

//    @RequestMapping("/admin")
//    public String admin(){
//        return "admin";
//    }

    @RequestMapping("/secure")
    public String secure(Principal principal, Model model){
        User myuser = ((CustomUserDetails)
                ((UsernamePasswordAuthenticationToken) principal)
                .getPrincipal()).getUser();
                model.addAttribute("myuser", myuser);
                return "secure";
    }

    //from cars
    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    CarRepository carRepository;

    @Autowired
    CloudinaryConfig cloudc;

    @RequestMapping("/")
    public String listCars(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("cars", carRepository.findAll());
        return "list";
    }

    @GetMapping("/add")
    public String carForm(Model model) {
        model.addAttribute("car", new Car());
        model.addAttribute("categories", categoryRepository.findAll());
        return "carform";
    }

    @PostMapping("/add")
    public String processMessage(@ModelAttribute Car car,
                                 @RequestParam("file") MultipartFile file){
        if (file.isEmpty()) {
            carRepository.save(car);
            return "redirect:/";
        }
        try{
            Map uploadResult = cloudc.upload(file.getBytes(),
                    ObjectUtils.asMap("resourcetype", "auto"));
            car.setCarpic(uploadResult.get("url").toString());
            carRepository.save(car);
        }catch (IOException e){
            e.printStackTrace();
            return "redirect:/add";
        }
        return  "redirect:/";
    }

    @PostMapping("/process")
    public String processForm(@Valid Car car, BindingResult result,
                              Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll());
            return "carform";
        }
        carRepository.save(car);
        return "redirect:/";
    }

    @GetMapping("/addcategory")
    public String categoryForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("categories", categoryRepository.findAll());
//        model.addAttribute("categories",categoryRepository.findByName(categoryName));

        return "categoryform";
    }

    @PostMapping("/processcategory")
    public String processCategory(@Valid Category category, BindingResult result,
                                  Model model) {
        if (result.hasErrors()) {
            return "categoryform";
        }
        categoryRepository.save(category);
        return "redirect:/add";

    }

    @RequestMapping("/detail/{id}")
    public String showCourse(@PathVariable("id") long id, Model model){
        model.addAttribute("car", carRepository.findById(id).get());
        return "show";
    }

    @RequestMapping("/update/{id}")
    public String updateCourse(@PathVariable("id") long id, Model model){
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("car", carRepository.findById(id).get());
        return "carform";
    }

    @RequestMapping("/delete/{id}")
    public String deleteCourse(@PathVariable("id") long id){
        carRepository.deleteById(id);
        return "redirect:/";
    }

}
