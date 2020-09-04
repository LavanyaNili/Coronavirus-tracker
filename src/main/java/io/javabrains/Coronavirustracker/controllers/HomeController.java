package io.javabrains.Coronavirustracker.controllers;

import io.javabrains.Coronavirustracker.models.LocationStats;
import io.javabrains.Coronavirustracker.services.CoronaVirusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
//this can also be @RestController if dealing with REST api's and json resposes/request.
public class HomeController {

    @Autowired
    CoronaVirusDataService CoronaVirusDataService ;

    @GetMapping("/")
    //when there is a get mapping, return the home template
    public String home(Model model)
    {
        // get the value that is being fetched in the service and add them to the model
        // to use the service first autowire the service
        //to calculate the total Corona cases by adding them
        List<LocationStats> allstats=CoronaVirusDataService.getAllStats();

        // getting the list of objects and converting it into a stream and then mapping it into an integer
        // Adding all the integers

        int total= allstats.stream().mapToInt(num ->Integer.parseInt(num.getLatestTotalCases())).sum();
        model.addAttribute("LocationStats", allstats);
        model.addAttribute("totalReportedCases",total);
        return "home";
    }
}
