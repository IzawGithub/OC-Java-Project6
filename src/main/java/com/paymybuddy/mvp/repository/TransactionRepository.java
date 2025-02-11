package com.paymybuddy.mvp.repository;

import com.paymybuddy.mvp.model.Transaction;
import com.paymybuddy.mvp.model.User;

import lombok.NonNull;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findAllBySenderIdOrReceiverId(UUID senderId, UUID receiverId);

    default List<Transaction> findAllByUser(@NonNull User user) {
        return findAllBySenderIdOrReceiverId(user.getId(), user.getId());
    }
}
