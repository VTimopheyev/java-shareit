package ru.practicum.shareit.item;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepository extends PagingAndSortingRepository<Item, Long> {

    List<Item> findAllByOwnerOrderById(User user, PageRequest pr);

    List<Item> findByNameContainingOrDescriptionContainingIgnoreCase(String text, String text1, PageRequest pr);

    List<Item> findByRequestEquals(ItemRequest i);

}
