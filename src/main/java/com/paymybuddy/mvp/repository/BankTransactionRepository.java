package com.paymybuddy.mvp.repository;

import com.paymybuddy.mvp.model.BankTransaction;
import com.paymybuddy.mvp.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BankTransactionRepository extends JpaRepository<BankTransaction, UUID> {
    List<BankTransaction> findAllByUser(User user);
}
