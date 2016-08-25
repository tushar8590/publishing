package com.sourcecode.spring.job;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.sourcecode.spring.job.MSPCatDataExtractor.DataExtractor;
import com.sourcecode.spring.model.MspElectronics;
import com.sourcecode.spring.model.MspProductUrl;
import com.sourcecode.spring.service.DownloadImagesService;
import com.sourcecode.spring.service.MspCatDataService;
import com.sourcecode.standalone.FillMspElectronicsColumns;

@Component
public class DownloadImages {

                @Autowired
                private DownloadImagesService downloadImagesService;
                private Map<String, String> downloadProductImagesMap;
                static int breakFactor = 30;

                @Async
                public void processData(List<String> sections) throws IOException {
                       LocalDateTime today = LocalDateTime.now();
                       System.out.println("Starting at " + today.toString());
                                processImages();
                                System.out.println("Ending at " + today.toString());

                }

                
                public void processImages() throws IOException{

                                
                                ExecutorService executor = Executors.newFixedThreadPool(30);
        List<Future<String>> futureSet = new ArrayList<>();

            System.out.println("Building MAster Map");
                                downloadProductImagesMap = downloadImagesService.populateImageMap();
                                    System.out.println(" Map COmpleted "+downloadProductImagesMap.size());
                                Set<String> masterModelSet = downloadProductImagesMap.keySet();

                                int a = 0;
                                int b = masterModelSet.size() / breakFactor;
                                
                                List<String> masterModelList = new ArrayList<String>();
                                masterModelList.addAll(masterModelSet);
                                int i = 0;
                //            for (int i = 0; i < breakFactor; i++) {
                                System.out.println("Going to Build Threads");
                                int k = 0;
                                while(i<=masterModelSet.size()){
                                  ArrayList<String> listn = new ArrayList<String>();

                                                listn.addAll(masterModelList.subList(a, b));
                                                
                                                a = b;
                                                b = b + masterModelList.size() / breakFactor;

                                                Callable<String> callable = this.new DownloadImage(listn);
                                                try{
                                                Future<String> future = executor.submit(callable);
             futureSet.add(future);
                                                }catch(RejectedExecutionException e){
                                                    e.printStackTrace();
                                                }
                                                //listn.clear();
                                                i = b; k++;
                                }
                                
                                System.out.println("Thread while loop completed  total threads "+k);
                                a = b;
        b = b + masterModelList.size();
                                List<String> listn = new ArrayList<>();
                                listn.addAll(masterModelList.subList(a, b));
                                   
                                Callable<String> callable = this.new DownloadImage(listn);
           Future<String> future = executor.submit(callable);
           futureSet.add(future);

                                futureSet.forEach(s->{
                                    try {
                System.out.println(s.get());
            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
                                });
                                
          executor.shutdown();
                                try {
                                    System.out.println("Awating Termination");
                                                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
                                }
                                catch (InterruptedException e) {
                                                System.out.println(e);
                                }
                                
                                System.out.println("Finished");

                }



                class DownloadImage implements Callable {

                   ArrayList<String> smallList = new ArrayList<String>();
                                public DownloadImage(List<String> listn){
                                    System.out.println("Inner class constructor called with "+listn.size());
                                                smallList.addAll(listn);
                                }


                                @Override
                                public Object call() throws Exception {

                                                Iterator<String> smallListIterator = smallList.iterator();
                                                
                                                while(smallListIterator.hasNext())
                                                {
                                                   
                                                                String model = smallListIterator.next();

                                                                String imageUrl = downloadProductImagesMap.get(smallListIterator.next());

                                                                String normalImageUrl =imageUrl;
                                                                String smallImageUrl =imageUrl.replace("-normal.jpg", "-big-thumb.jpg");
                                                                String zoomImageUrl =imageUrl.replace("-normal.jpg", "-zoom.jpg");

                                                                String normalImageSaveDir = "c://aap_product_images_normal//";///aapcompare_Microtek-EMT2090-Voltage-Stabilizer.jpg;
                                                                String smallImageSaveDir = "c://aap_product_images_small//";///aapcompare_Microtek-EMT2090-Voltage-Stabilizersmall.jpg;
                                                                String zoomImageSaveDir = "c://aap_product_images_zoom//";///aapcompare_Microtek-EMT2090-Voltage-Stabilizer_big.jpg;

                                                                String normalImageName =normalImageSaveDir+"aapcompare_"+model+".jpg";
                                                                String smallImageName =smallImageSaveDir+"aapcompare_"+model+"_small.jpg";
                                                                String zoomImageName =zoomImageSaveDir+"aapcompare_"+model+"_big.jpg";

                                                                downloadImage(normalImageUrl,normalImageName);
                                                                downloadImage(smallImageUrl,smallImageName);
                                                                downloadImage(zoomImageUrl,zoomImageName);
                                                }


                                                return null;
                                }
                }

                public void downloadImage(String imageUrl, String imageName){
                    try{
                                URL url = new URL(imageUrl);
                                HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
                                int responseCode = httpConn.getResponseCode();
                                
                                // always check HTTP response code first
                                if (responseCode == HttpURLConnection.HTTP_OK) {
                                                String fileName = "";
                                                String disposition = httpConn.getHeaderField("Content-Disposition");
                                                String contentType = httpConn.getContentType();
                                                int contentLength = httpConn.getContentLength();

                                                if (disposition != null) {
                                                                // extracts file name from header field
                                                                int index = disposition.indexOf("filename=");
                                                                if (index > 0) {
                                                                                fileName = disposition.substring(index + 10,
                                                                                                                disposition.length() - 1);
                                                                }
                                                } else {
                                                                // extracts file name from URL
                                                                fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1,
                                                                                                imageUrl.length());
                                                }
                                }


                                BufferedImage image = ImageIO.read(httpConn.getInputStream());

                                File compressedImageFile = new File(imageName);
                                OutputStream os =new FileOutputStream(compressedImageFile);

                                Iterator<ImageWriter>writers =  ImageIO.getImageWritersByFormatName("jpg");
                                ImageWriter writer = writers.next();

                                ImageOutputStream ios;

                                ios = ImageIO.createImageOutputStream(os);

                                writer.setOutput(ios);

                                ImageWriteParam param = writer.getDefaultWriteParam();

                                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                                param.setCompressionQuality(0.5f);
                                writer.write(null, new IIOImage(image, null, null), param);

                                os.close();
                                ios.close();
                                writer.dispose();
                    }catch(IOException e){
                                    System.out.println(e.getMessage());
                                   
                                }

                }

   
}
