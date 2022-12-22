package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;

public interface ItemRequestRepository extends PagingAndSortingRepository<ItemRequest, Long> {
    Collection<ItemRequest> findByUserEqualsOrderByCreatedDesc(User user);

    List<ItemRequest> findAllByUserNot(User user, PageRequest pr);
}
