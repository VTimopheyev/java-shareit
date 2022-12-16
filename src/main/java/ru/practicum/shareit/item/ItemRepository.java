package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwnerEquals(User user);

    List<Item> findByNameContainingOrDescriptionContainingIgnoreCaseAndAvailableEquals(String text, String text2, boolean available);
}
