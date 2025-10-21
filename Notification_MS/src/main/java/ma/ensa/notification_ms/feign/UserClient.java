package ma.ensa.notification_ms.feign;



import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;


import java.util.List;


@FeignClient(name = "borrower-ms")  // registered name in Eureka
public interface UserClient {


    @GetMapping("/api/users/ids/users")
    List<Long> GetUsersIds();

    @GetMapping("/api/users/ids/admins")
    List<Long> GetAdminIds();

}

