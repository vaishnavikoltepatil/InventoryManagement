package com.Inventory_management_System.controller;

import com.Inventory_management_System.auth.entities.ForgotPassword;
import com.Inventory_management_System.auth.entities.User;
import com.Inventory_management_System.auth.repositories.ForgotPasswordRepository;
import com.Inventory_management_System.auth.repositories.UserRepository;
import com.Inventory_management_System.auth.services.EmailService;
import com.Inventory_management_System.dto.ChangePasswordwithOld;
import com.Inventory_management_System.dto.MailBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

@Controller
@RequestMapping("/forgotPassword")
public class ForgotPasswordController {

    private final UserRepository userRepository;

    private final EmailService emailService;

    private final ForgotPasswordRepository forgotPasswordRepository;

    private final PasswordEncoder passwordEncoder;

    public ForgotPasswordController(UserRepository userRepository, EmailService emailService, ForgotPasswordRepository forgotPasswordRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.forgotPasswordRepository = forgotPasswordRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //send mail to email verification

    @PostMapping("/verifyMail/{email}")
    public ResponseEntity<String> verifyEmail(@PathVariable String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("please provide an valid email!"));
        int otp = otpGenerator();
        MailBody mailBody = MailBody.builder()
                .to(email)
                .text("This is the OTP for your forgot password request: " + otp)
                .subject("OTP for Forgot request")
                .build();

        ForgotPassword fp = ForgotPassword.builder()
                .otp(otp)
                .expirationTime(new Date(System.currentTimeMillis() + 70 * 1000))
                .user(user)
                .build();

        emailService.sendSimpleMessage(mailBody);
        forgotPasswordRepository.save(fp);

        return ResponseEntity.ok("Email sent for verification!!");

    }

    @PostMapping("/verifyOtp/{otp}/{email}")
    public ResponseEntity<String> verifyOtp(@PathVariable Integer otp, @PathVariable String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("please provide an valid email!"));

        ForgotPassword fp = forgotPasswordRepository.
                findByOtpAndUser(otp, user)
                .orElseThrow(() -> new RuntimeException(("Invalid OTP for email" + email)));

        if (fp.getExpirationTime().before(Date.from(Instant.now()))) {
            forgotPasswordRepository.deleteById(fp.getFpid());
            return new ResponseEntity<>("OTP has expired!", HttpStatus.EXPECTATION_FAILED);

        }
        return ResponseEntity.ok("OTP verified!");


    }

    @PostMapping("/changePassword/{email}")
    public ResponseEntity<String> changePasswordHandler(@RequestBody ChangePasswordwithOld changePassword,
                                                        @PathVariable String email) {

        if (!Objects.equals(changePassword.password(), changePassword.ConfirmPassword())) {
            return new ResponseEntity<>("please enter the password again!", HttpStatus.EXPECTATION_FAILED);
        }

        String encodedPassword = passwordEncoder.encode(changePassword.password());
        userRepository.updatePassword(email, encodedPassword);

        return ResponseEntity.ok("Password has been changed!");
    }


    @PostMapping("/updatePassword/{email}")
    public ResponseEntity<String> updatePassword(@PathVariable String email, @RequestBody ChangePasswordwithOld changePassword) {
        //  Retrieve user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with email " + email + " not found"));

        // Verify if the old password matches the one stored in the database
        if (!passwordEncoder.matches(changePassword.oldPassword(), user.getPassword())) {
            return new ResponseEntity<>("Old password is incorrect!", HttpStatus.BAD_REQUEST);
        }

        //  Validate that the new password and confirm password match
        if (!Objects.equals(changePassword.password(), changePassword.ConfirmPassword())) {
            return new ResponseEntity<>("New password and confirmation password do not match!", HttpStatus.BAD_REQUEST);
        }

        //  the new password and update it in the database
        String encodedNewPassword = passwordEncoder.encode(changePassword.password());
        user.setPassword(encodedNewPassword);
        userRepository.save(user);

        return ResponseEntity.ok("Password has been updated successfully!");
    }



    private Integer otpGenerator(){
        Random random=new Random();
        return random.nextInt(100_000,999_999);

    }
}
