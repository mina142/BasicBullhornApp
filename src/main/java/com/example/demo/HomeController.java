package com.example.demo;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    MessageRepository messageRepository;

    @Autowired
    CloudinaryConfig cloudc;

    @RequestMapping("/")
    public String listMessages(Model model){
        model.addAttribute("messages" , messageRepository.findAll());
        return "list";
    }
    @PostMapping("/searchlist")
    public String search(Model model, @RequestParam("search") String search){
        model.addAttribute("messages" , messageRepository.findByContentContainingIgnoreCaseOrPostedDateContainingIgnoreCase(search,search));
        return "searchlist";
    }
    @GetMapping("/add")
    public String messageForm(Model model){
        model.addAttribute("message", new Message());
        return "messageForm";
    }
    @PostMapping("/process")
        public String processMessage(@Valid @ModelAttribute Message message, BindingResult result,
                                     @RequestParam("file")MultipartFile file){
            if(result.hasErrors()){
                return "messageForm";
            }
        if(file.isEmpty()){
            messageRepository.save(message);
            return "redirect:/";
        }
        try{
            Map uploadResult = cloudc.upload(file.getBytes(),
                    ObjectUtils.asMap("resourcetype", "auto"));
             message.setHeadshot(uploadResult.get("url").toString());
             messageRepository.save(message);
        }catch(IOException e){
            e.printStackTrace();
            return "messageForm";
        }
        return "redirect:/";
        }


    @RequestMapping("/detail/{id}")
    public String showMessage(@PathVariable("id") long id, Model model){
        model.addAttribute("message", messageRepository.findById(id).get());
        return "show";
    }
    @RequestMapping("/update/{id}")
    public String updateMessage(@PathVariable("id") long id, Model model){
        model.addAttribute("message" , messageRepository.findById(id).get());
        return "messageForm";
    }
    @RequestMapping("/delete/{id}")
    public String deleteMessage(@PathVariable("id") long id, Model model){
        messageRepository.deleteById(id);
        return "redirect:/";
    }


}
