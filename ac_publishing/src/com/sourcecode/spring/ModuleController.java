package com.sourcecode.spring;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import com.sourcecode.spring.job.CronJobExample;
import com.sourcecode.spring.job.DownloadImages;
import com.sourcecode.spring.job.MSPCatDataExtractor;
import com.sourcecode.spring.job.MSPSpecLoader;
import com.sourcecode.spring.job.MSPUrlExtractor;
import com.sourcecode.spring.model.ElectronicsPriceUpdater;
import com.sourcecode.spring.model.Module;
import com.sourcecode.spring.model.NewMenu;
import com.sourcecode.spring.model.User;
import com.sourcecode.spring.service.CategoryService;
import com.sourcecode.standalone.AmazonProductPriceUpdater;
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
    
    @Autowired
    private DownloadImages downloadImages;

    private List<String>optionList =Arrays.asList(FunctionConstants.URLExtractor,FunctionConstants.CatDataExtractor,
    		FunctionConstants.SpecLoader,
    		FunctionConstants.urlResolver,FunctionConstants.downloadImages,FunctionConstants.priceUpdater);
    
    private List priceUpdaterSelectionList = Arrays.asList(
    		 FunctionConstants.priceUpdaterFlipkart,
FunctionConstants.priceUpdaterSnapdeal ,
FunctionConstants.priceUpdaterAmazon ,
FunctionConstants.priceUpdaterOthers);
   private List<PriceUpdatertype> priceUpdaterTypeList = Arrays.asList(PriceUpdatertype.DAILY,PriceUpdatertype.WEEKLY); 
    
    @RequestMapping(value = "loadDashboard")
    public ModelAndView loadDashboard(){
        ModelAndView model = new ModelAndView();
        model.addObject("module",new Module());
        model.addObject("radioList",optionList);
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
        }else if(module.getModuleName().equalsIgnoreCase(FunctionConstants.downloadImages)){
            model.addObject("newMenuAttribute", new NewMenu());
            model.addObject("categoryList",categoryService.getCategoryList());
            model.addObject("modelName", FunctionConstants.downloadImages);
            model.setViewName("elecModule");
        }else if(module.getModuleName().equalsIgnoreCase(FunctionConstants.priceUpdater)){
            model.addObject("electronicsPriceUpdater",new ElectronicsPriceUpdater());
            model.addObject("updaterSelectionList",priceUpdaterSelectionList);
            model.addObject("updaterTypeList",priceUpdaterTypeList);
            model.setViewName("priceUpdater");
        }
        
        return model;
    }
    
    
    @RequestMapping(value = "startPriceUpdaterAction", method=RequestMethod.POST)
    public ModelAndView startPriceUpdaterAction(@ModelAttribute("electronicsPriceUpdater") ElectronicsPriceUpdater priceUpdater) {
    	ModelAndView model = new ModelAndView();
    	 model.addObject("processName", "Cat Data Extractor");
         model.setViewName("ProcessRunning");
         AmazonProductPriceUpdater amazonUpdater = new AmazonProductPriceUpdater();
         amazonUpdater.execute(priceUpdater.getUpdaterType());
         return model;
    }
    
    @RequestMapping(value = "startELectronicsDataUpdate", method=RequestMethod.POST)
    public ModelAndView startELectronicsDataUpdate(@ModelAttribute("newMenuAttribute") NewMenu menu) throws IOException{
        ModelAndView model = new ModelAndView();
        List<String> sections = null;
        if(menu.getAllCats() != null && menu.getAllCats()  != ""){
        	sections = getAllSections();
        }else{
        System.out.println(menu.getSection());
         sections = Arrays.asList(menu.getSection());
        }
        if(menu.getSubModuleName().equalsIgnoreCase(FunctionConstants.URLExtractor)){
            mspUrlExtractor.processData(sections);
            model.addObject("processName", "MSP Url Extractor");
            model.setViewName("ProcessRunning");
        }else if(menu.getSubModuleName().equalsIgnoreCase(FunctionConstants.CatDataExtractor)){
            mspCatDataExtractor.processData(sections);
            model.addObject("processName", "Cat Data Extractor");
            model.setViewName("ProcessRunning");
            
        }else if(menu.getSubModuleName().equalsIgnoreCase(FunctionConstants.SpecLoader)){
            specLoader.processData(sections);
            model.addObject("processName", "Spec Loader");
            model.setViewName("ProcessRunning");
        }else if(menu.getSubModuleName().equalsIgnoreCase(FunctionConstants.urlResolver)){
           // specLoader.processData(Arrays.asList(menu.getSection()));
            MspUrlResolver.execute();
            model.addObject("processName", "URL Resolver ");
            model.setViewName("ProcessRunning");
        }else if(menu.getSubModuleName().equalsIgnoreCase(FunctionConstants.downloadImages)){
        	downloadImages.processData(sections);
             model.addObject("processName", "Download Images");
             model.setViewName("ProcessRunning");
        }
        
       
         return model;
    }
    private List<String> getAllSections(){
    	 
    	Map<String,String> catsMap = categoryService.getCategoryList();
    	List<String> sections = new ArrayList(catsMap.keySet());
    	return sections;
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
