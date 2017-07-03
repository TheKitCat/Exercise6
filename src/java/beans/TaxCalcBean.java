/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * Class, which writes a tax record into a csv file
 * Attention: Works with wWldfly v10 and NetBeans 8.2 (NetBeans 8.1. causes Exceptions)
 * @author katharina
 */
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "java:jboss/exported/jms/queue/taxCalc")
    ,
        @ActivationConfigProperty(propertyName = "subscriptionDurability", propertyValue = "Durable")
    ,
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class TaxCalcBean implements MessageListener {
    
    public TaxCalcBean() {
    }
    
    /**
     * Method is called after a new message arrives. Writes the tax record into a file
     * @param message 
     */
    @Override
    public void onMessage(Message message) {
        try {
            
            System.out.println("Receive message");
            
            String record = (String) message.getBody(String.class);
            
            File file = new File("records_mdb.csv");
            
            if(!file.exists()){
                file.createNewFile();
            }
            
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.append(record+"\n"); 
            bw.close();
            
        } catch (JMSException ex) {
            Logger.getLogger(TaxCalcBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TaxCalcBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
