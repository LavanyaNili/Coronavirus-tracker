package io.javabrains.Coronavirustracker.services;

import io.javabrains.Coronavirustracker.models.LocationStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

// This is a service hence we need to annotate the service using @service to tell spring boot that this is a service

@Service

public class CoronaVirusDataService {
    //store the URL in a private variable
    private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

    /*
    method to create a Http request
    create an object of http client and create a new request using httprequest
    Convert the URL string to a URI
     */



    // We need to tell spring boot to start this method as soon as it creates the object/ constructor of the service.
    // We need an array of all the model class LocationStats
    private List<LocationStats> allStats= new ArrayList<>();
    public List<LocationStats> getAllStats() {
        return allStats;
    }

    @PostConstruct
    @Scheduled(cron = "* * 1 * * *")
    // the stars stands for sec, min, hour, day, month, year
    // now it it will run 1st hour of every day
    //to schedule it every second
    public void fetchVirusData() throws IOException, InterruptedException {
        // you cannot just drop the stat because many people might be hitting the URL to get the stats.
        //hence copy it in a new array list and use it
         List<LocationStats> newStats= new ArrayList<>();

        HttpClient client = HttpClient.newHttpClient();
        //convert the URL string to URI by using URI.cretae method.
        //create a request using teh build up pattern
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(VIRUS_DATA_URL))
                .build();
        //now use send the request using the client object
        //the send method takes the request and the body handler of the response
        //take the body and return as a string
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        // client.send can fail to send the request to the client hence throws some exception
        // We can also print the body of the response
        //System.out.println(httpResponse.body());
        StringReader CSV_read = new StringReader(httpResponse.body());
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(CSV_read);
        for (CSVRecord record : records) {
            LocationStats stats = new LocationStats();
            stats.setState(record.get("Province/State"));
            stats.setCountry(record.get("Country/Region"));
            int latestCases= Integer.parseInt(record.get(record.size()-1));
            int prevDayCases= Integer.parseInt(record.get(record.size()-2));
            stats.setLatestTotalCases(Integer.toString(latestCases));
            stats.setDiffFromPrevDay(latestCases-prevDayCases);
            newStats.add(stats);

        }
        this.allStats=newStats;
    }
}
