package com.demoflowablespringtips.demoflowablespringtips.processes;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

@Service
class EmailService{
	
	ConcurrentHashMap<String, AtomicInteger> sends = new ConcurrentHashMap<String, AtomicInteger>();
	
	public void sendWelcomeEmail(String customerId,String email){
		//log.info("sending welcome email for: " + customerId + " to "+ email));	
		System.out.println("sending welcome email for: " + customerId + " to "+ email);	
		sends.computeIfAbsent(email, e-> new AtomicInteger());
		sends.get(email).incrementAndGet();
	
	}
}
