package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.OwnerItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserNotFoundException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.UserValidationException;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;
    private final CommentsRepository commentsRepository;


    public ItemDto addNewItem(Optional<Long> userId, ItemDto itemDTO) {
        if (userId.isEmpty()) {
            throw new UserValidationException();
        }

        if (!userService.checkUserExists(userId.get())) {
            throw new UserNotFoundException();
        }

        if (validateItemDTO(itemDTO)) {
            Item item = itemMapper.toItem(itemDTO);
            item.setOwner(userService.getOwner(userId.get()));
            itemRepository.save(item);
            List<Comment> comments = commentsRepository.findByItemEquals(item);

            return itemMapper.toItemDto(item, comments);
        }
        return null;
    }

    public OwnerItemDto getItemForOwner(Item item, BookingDto lastBooking, BookingDto nextBooking) {
        List<Comment> comments = commentsRepository.findByItemEquals(item);
        return itemMapper.toOwnerItemDto(item, lastBooking, nextBooking, comments);
    }

    @Override
    public Comment addNewComment(Optional<Long> userId, long itemId, Comment comment) {

        if (comment.getText().isEmpty()) {
            throw new CommentValidationException();
        }

        comment.setCreated(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));
        comment.setAuthorName(userRepository.findById(userId.get()).get().getName());
        comment.setItem(itemRepository.findById(itemId).get());

        return commentsRepository.save(comment);
    }

    @Override
    public OwnerItemDto convertToOwnerItemDto(Item i, BookingDto lastBooking, BookingDto nextBooking) {
        List<Comment> comments = commentsRepository.findByItemEquals(i);
        return itemMapper.toOwnerItemDto(i, lastBooking, nextBooking, comments);
    }

    @Override
    public List<Item> getAllItems(User user) {
        return itemRepository.findByOwnerEquals(user);
    }

    public ItemDto updateItem(Optional<Long> userId, long itemId, ItemDto itemDTO) {
        if (userId.isEmpty() || !userService.checkUserExists(userId.get()) ||
                Optional.of(itemRepository.findById(itemId)).isEmpty()) {
            throw new UserValidationException();
        }

        if (!checkItemOwnedByUser(userId, itemId)) {
            throw new ItemNotFoundException();
        }

        Item item = itemRepository.findById(itemId).get();
        if (itemDTO.getName() != null) {
            item.setName(itemDTO.getName());
        }
        if (itemDTO.getAvailable() != null) {
            item.setAvailable(itemDTO.getAvailable());
        }
        if (itemDTO.getDescription() != null) {
            item.setDescription(itemDTO.getDescription());
        }

        List<Comment> comments = commentsRepository.findByItemEquals(item);
        return itemMapper.toItemDto(itemRepository.save(item), comments);
    }

    public void deleteItem(Optional<Long> userId, long itemId) {
        if (userId.isEmpty() || !userService.checkUserExists(userId.get()) ||
                Optional.of(itemRepository.findById(itemId)).isEmpty()) {
            throw new UserValidationException();
        }

        if (!checkItemOwnedByUser(userId, itemId)) {
            throw new ItemNotFoundException();
        }
        itemRepository.deleteById(itemId);
    }


    public List<ItemDto> searchItems(Optional<Long> userId, String text) {
        if (userId.isEmpty() || !userService.checkUserExists(userId.get())) {
            throw new UserValidationException();
        }

        if (text.isEmpty()) {
            return new ArrayList<>();
        }

        List<Item> searchedItems =
                itemRepository.findByNameContainingOrDescriptionContainingIgnoreCaseAndAvailableEquals(text, text, true);
        List<ItemDto> searchedItemsDto = new ArrayList<>();
        for (Item i : searchedItems) {
            List<Comment> comments = commentsRepository.findByItemEquals(i);
            searchedItemsDto.add(itemMapper.toItemDto(i, comments));
        }

        return searchedItemsDto;
    }

    public Optional<Item> getItemById(Long id) {
        return itemRepository.findById(id);
    }

    private boolean validateItemDTO(ItemDto itemDTO) {
        if (itemDTO.getName() == null || itemDTO.getName().isEmpty()) {
            throw new ItemValidationException();
        }
        if (itemDTO.getDescription() == null || itemDTO.getDescription().isEmpty()) {
            throw new ItemValidationException();
        }
        if (itemDTO.getAvailable() == null) {
            throw new ItemValidationException();
        }
        return true;
    }

    @Override
    public boolean checkItemExists(Long id) {
        return itemRepository.existsById(id);
    }

    private boolean checkItemOwnedByUser(Optional<Long> userId, Long itemId) {
        return userId.get() == (itemRepository.findById(itemId).get().getOwner().getId());
    }
}


