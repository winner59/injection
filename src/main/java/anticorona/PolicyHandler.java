package anticorona;

import anticorona.config.kafka.KafkaProcessor;

import java.util.Optional;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PolicyHandler{

    @Autowired
    InjectionRepository injectionRepository;


    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverBooked_AcceptBooking(@Payload Booked booked){

        if(!booked.validate()) return;

        System.out.println("\n\n##### listener AcceptBooking : " + booked.toJson() + "\n\n");

        // 접종준비(AcceptBooking) //
        
        Injection injection = new Injection();

        injection.setStatus("INJECTION_READY");
        injection.setBookingId(booked.getBookingId());            
        injection.setVaccineId(booked.getVaccineId());
        injection.setUserId(booked.getUserId());

        injectionRepository.save(injection);     
          
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverBookingCancelled_CancelBooking(@Payload BookingCancelled bookingCancelled){

        if(!bookingCancelled.validate()) return;

        System.out.println("\n\n##### listener AcceptCancelBooking : " + bookingCancelled.toJson() + "\n\n");

        // 접종예약취소(AcceptCancelBooking) //        
        Optional<Injection> injectionOptional = injectionRepository.findByBookingId(bookingCancelled.getBookingId());
        if(injectionOptional.isPresent()){
            Injection injection = injectionOptional.get();
            injection.setStatus("BOOKING_CANCELLED");
            injectionRepository.save(injection);
        }            
        
    }

}
