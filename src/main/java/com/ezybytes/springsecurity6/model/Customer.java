package com.ezybytes.springsecurity6.model;

import java.time.LocalDate;
import java.util.Set;

import org.hibernate.annotations.GenericGenerator;

import net.sf.jsqlparser.expression.DateTimeLiteralExpression;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Customer {
	@Id
	@GeneratedValue(strategy= GenerationType.AUTO,generator="native")
	@GenericGenerator(name = "native")
	@Column(name = "customer_id")
	private int id;

	private String name;

	private String email;

	@Column(name = "mobile_number")
	private String mobileNumber;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)//요청할 때만 응답시는 내랴보내지 않은 @JsonIgnore
	private String pwd;

	private String role;

	@Column(name = "create_dt")
	private LocalDate createDt;

	@JsonIgnore
	@OneToMany(mappedBy = "customer", fetch = FetchType.EAGER)
	private Set<Authority> authorities;
}
