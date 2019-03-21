package com.example.demo;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    private UserService userService;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    CarRepository carRepository;
    @Autowired
    CloudinaryConfig cloudc;

    //start at index which shows the login page

    @GetMapping("/")
    public String index() {

        return "list";
    }

    //if you click new user you go to the registration page

    @GetMapping ("/register")
    public String registrationPage(Model model){
        model.addAttribute("user", new User());
        return "registration";
    }

    //if there are errors in registration stay at registration otherwise go to "login" page
    @PostMapping ("/register")
    public String processRegistrationPage(@Valid
                                           @ModelAttribute("user") User user,
                                           BindingResult result,
                                           Model model){
        model.addAttribute("user", new User());
        if (result.hasErrors())
        {
            return "registration";
        }
        else
        {
            userService.saveUser(user);
            model.addAttribute("message", "User Account Created");
        }
        return "list";
    }


//    @RequestMapping("/")
//    public String index(){
//        return "index";
//    }
    @RequestMapping("/login")
    public String loginpage(){
        return "login";
    }
    @PostMapping("/login")
    public String login(Principal principal, Model model){
        User myuser = ((CustomUserDetails)
                ((UsernamePasswordAuthenticationToken) principal)
                        .getPrincipal()).getUser();
        model.addAttribute("myuser", myuser);
        return "list";
    }

//    @RequestMapping("/admin")
//    public String admin(){
//        return "admin";
//    }

    //loggin security

    @RequestMapping("/list")
    public String secure(Model model){
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("cars", carRepository.findAll());
        if(userService.getUser() != null) {
            model.addAttribute("user_id", userService.getUser().getId());
        }
        return "list";

    }

    //search
    @PostMapping("/search")
    public String searchword(Model model, @RequestParam String search){
        ArrayList<Car> results =(ArrayList<Car>)
                carRepository.findByModelOrManufacturerContainingIgnoreCase(search,search);
        model.addAttribute("cars", results);
        return "list";
    }

    //user profiles for logged in user

    @RequestMapping("/profile")
    public String profile(Model model){
        if(userService.getUser() != null) {
            model.addAttribute("user", userService.getUser());
        }
        return "profile";
    }


    //list of all cars mapping

//    @RequestMapping("/list")
//    public String listCars(Model model) {
//        model.addAttribute("categories", categoryRepository.findAll());
//        model.addAttribute("cars", carRepository.findAll());
//        if(userService.getUser() != null) {
//            model.addAttribute("user_id", userService.getUser().getId());
//        }
//        return "list";
//    }

    //go to add a category and go to category form

    @GetMapping("/addcategory")
    public String catForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("cars",carRepository.findAll());
        return "categoryform";
    }

    //go for adding a car and going to car form

    @GetMapping("/addcar")
    public String carForm(Model model) {
        model.addAttribute("car", new Car());
        model.addAttribute("categories", categoryRepository.findAll());
        return "carform";
    }

    //process the contents of the car form so it can be posted

    @PostMapping("/processcarfile")
    public String processMessage(@ModelAttribute Car car,
                                 @RequestParam("file") MultipartFile file){
        if (file.isEmpty()) {
            carRepository.save(car);
            return "redirect:/list";
        }
        try{
            Map uploadResult = cloudc.upload(file.getBytes(),
                    ObjectUtils.asMap("resourcetype", "auto"));
            car.setCarpic(uploadResult.get("url").toString());
            carRepository.save(car);
        }catch (IOException e){
            e.printStackTrace();
            return "redirect:/addcategory";
        }
        return  "redirect:/list";
    }

    @PostMapping("/process")
    public String processForm(@Valid Car car, BindingResult result,
                              Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll());
            return "carform";
        }
        car.setUser(userService.getUser());
        carRepository.save(car);
        return "redirect:/list";
    }


    @PostMapping("/processcategory")
    public String processCategory(@Valid Category category, BindingResult result,
                                  Model model) {
        if (result.hasErrors()) {
            return "categoryform";
        }
        //save category and add car
        categoryRepository.save(category);
        return "redirect:/addcar";

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
