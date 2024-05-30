package com.insure.rfq.login.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "login_data")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class UserLogin {

	@Id
	@SequenceGenerator(name = "mySeqGen", initialValue = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mySeqGen")
	private long id;
	@NotNull(message = "username must be entered")
	@Pattern(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+\\.com$", message = "Email is not valid")
	@NotEmpty(message = "Email cannot be empty")
	private String email;
	@NotNull(message = "password must be entered")
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%!^&*()])[A-Za-z\\d@#$%!^&*()]{5,11}$", message = "Password must be at least 8 characters long and contain at least one uppercase letter, one number, and one special character.")
	private String password;
	
}
