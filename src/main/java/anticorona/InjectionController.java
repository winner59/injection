package anticorona;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

 @RestController
 public class InjectionController {

     @Autowired
     InjectionRepository injectionRepository;

     @RequestMapping(value = "/injections/checkStatus",
        method = RequestMethod.GET,
        produces = "application/json;charset=UTF-8")
    public boolean checkAndBookStock(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("##### /injection/checkStaus  called #####");

        boolean status = false;

        Long bookingId = Long.valueOf(request.getParameter("bookingId"));
        
        Optional<Injection> injection = injectionRepository.findById(bookingId);
        if(injection .isPresent()){
            Injection injectionValue = injection.get();

            //증명서 발급 가능한지 체크 
            if(injectionValue.getStatus() =="INJECTION_COMPLETED") {
                //발급  가능하면 예약수량 증가
                status = true;
                //injectionValue.setStatus(injectionValue.getBookQty() + 1);
                injectionRepository.save(injectionValue);
            }
        }

        return status;
     }
 }
