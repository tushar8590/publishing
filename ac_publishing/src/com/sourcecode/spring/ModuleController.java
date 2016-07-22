package com.sourcecode.spring;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import com.sourcecode.spring.job.CronJobExample;
import com.sourcecode.spring.job.MSPCatDataExtractor;
import com.sourcecode.spring.job.MSPSpecLoader;
import com.sourcecode.spring.job.MSPUrlExtractor;
import com.sourcecode.spring.model.Module;
import com.sourcecode.spring.model.NewMenu;
import com.sourcecode.spring.model.User;
import com.sourcecode.spring.service.CategoryService;
import com.sourcecode.standalone.MspUrlResolver;



@Controller
public class ModuleController {
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private MSPUrlExtractor mspUrlExtractor;
    
    @Autowired
    private MSPCatDataExtractor mspCatDataExtractor;
    
    @Autowired
    private MSPSpecLoader specLoader;

    
    @RequestMapping(value = "loadDashboard")
    public ModelAndView loadDashboard(){
        ModelAndView model = new ModelAndView();
        model.addObject("module",new Module());
        model.addObject("radioList",Arrays.asList(FunctionConstants.URLExtractor,FunctionConstants.CatDataExtractor,FunctionConstants.SpecLoader,FunctionConstants.urlResolver));
        model.setViewName("dashboard");
        return model;
    }
    // user choice for the module
    @RequestMapping(value = "loadModule", method = RequestMethod.POST)
    public ModelAndView loadModule(Module module){
        ModelAndView model = new ModelAndView();
        model.addObject("module",module);
        if(module.getModuleName().equalsIgnoreCase(FunctionConstants.URLExtractor)){
            model.addObject("newMenuAttribute", new NewMenu());
            model.addObject("categoryList",categoryService.getCategoryList());
            model.addObject("modelName", FunctionConstants.URLExtractor);
            model.setViewName("elecModule");
        }
        else if(module.getModuleName().equalsIgnoreCase(FunctionConstants.CatDataExtractor)){
            model.addObject("newMenuAttribute", new NewMenu());
            model.addObject("categoryList",categoryService.getCategoryList());
            model.addObject("modelName", FunctionConstants.CatDataExtractor);
            model.setViewName("elecModule");
        }else if(module.getModuleName().equalsIgnoreCase(FunctionConstants.SpecLoader)){
            model.addObject("newMenuAttribute", new NewMenu());
            model.addObject("categoryList",categoryService.getCategoryList());
            model.addObject("modelName", FunctionConstants.SpecLoader);
            model.setViewName("elecModule");
        }else if(module.getModuleName().equalsIgnoreCase(FunctionConstants.urlResolver)){
            model.addObject("newMenuAttribute", new NewMenu());
            model.addObject("categoryList",categoryService.getCategoryList());
            model.addObject("modelName", FunctionConstants.urlResolver);
            model.setViewName("elecModule");
        }
        
        return model;
    }
    
    
    @RequestMapping(value = "startELectronicsDataUpdate", method=RequestMethod.POST)
    public ModelAndView startELectronicsDataUpdate(@ModelAttribute("newMenuAttribute") NewMenu menu){
        ModelAndView model = new ModelAndView();
        System.out.println(menu.getSection());
        
        if(menu.getSubModuleName().equalsIgnoreCase(FunctionConstants.URLExtractor)){
            mspUrlExtractor.processData(Arrays.asList(menu.getSection()));
            model.addObject("processName", "MSP Url Extractor");
            model.setViewName("ProcessRunning");
        }else if(menu.getSubModuleName().equalsIgnoreCase(FunctionConstants.CatDataExtractor)){
            mspCatDataExtractor.processData(Arrays.asList(menu.getSection()));
            model.addObject("processName", "Cat Data Extractor");
            model.setViewName("ProcessRunning");
            
        }else if(menu.getSubModuleName().equalsIgnoreCase(FunctionConstants.SpecLoader)){
            specLoader.processData(Arrays.asList(menu.getSection()));
            model.addObject("processName", "Spec Loader");
            model.setViewName("ProcessRunning");
        }else if(menu.getSubModuleName().equalsIgnoreCase(FunctionConstants.urlResolver)){
           // specLoader.processData(Arrays.asList(menu.getSection()));
            MspUrlResolver.execute();
            model.addObject("processName", "URL Resolver ");
            model.setViewName("ProcessRunning");
        }
        
       
         return model;
    }
    
    @RequestMapping(value = "startJob", method=RequestMethod.GET)
    public ModelAndView startCronJob(@ModelAttribute("userAttribute") User user,HttpServletRequest request, SessionStatus sessionStatus){
        ModelAndView model = new ModelAndView();
        // check sessionStatus
        CronJobExample ct = new CronJobExample();
        ct.demoService();
        model.addObject("msg","Job Started");
        model.addObject("user", user);
        model.setViewName("dashboard");
        return model;
    }
}
