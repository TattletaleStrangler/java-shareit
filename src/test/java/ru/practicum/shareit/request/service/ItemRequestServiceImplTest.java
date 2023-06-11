package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.AddItemRequestDto;
import ru.practicum.shareit.request.dto.GetItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class ItemRequestServiceImplTest {

    private final EntityManager em;
    private final ItemRequestService itemRequestService;
    private final UserService userService;

    @Test
    void createItemRequest() {
        AddItemRequestDto addItemRequestDto = new AddItemRequestDto("Описание запроса");
        UserDto userDto = UserDto.builder()
                .name("Serj Tankian")
                .email("serjtankian@mail.com")
                .build();

        final Long itemRequestId = 1L;
        LocalDateTime start = LocalDateTime.now();

        UserDto savedUserDto = userService.createUser(userDto);
        itemRequestService.createItemRequest(addItemRequestDto, savedUserDto.getId());

        TypedQuery<ItemRequest> query = em.createQuery("Select i from ItemRequest i where i.id = :id", ItemRequest.class);
        ItemRequest itemRequest = query.setParameter("id", itemRequestId)
                .getSingleResult();

        LocalDateTime end = LocalDateTime.now();

        assertThat(itemRequest, notNullValue());
        assertThat(itemRequest.getId(), equalTo(itemRequestId));
        assertThat(itemRequest.getDescription(), equalTo(addItemRequestDto.getDescription()));
        assertThat(itemRequest.getCreated(), greaterThan(start));
        assertThat(itemRequest.getCreated(), lessThan(end));
    }

    @Test
    void findAllByRequesterId() {
        UserDto requesterDto1 = UserDto.builder()
                .name("Serj Tankian")
                .email("serjtankian@mail.com")
                .build();
        UserDto requesterDto2 = UserDto.builder()
                .name("John Dolmayan")
                .email("johndolmayan@mail.com")
                .build();
        UserDto ownerDto1 = UserDto.builder()
                .name("Daron Malakian")
                .email("daronmalakian@mail.com")
                .build();
        UserDto ownerDto2 = UserDto.builder()
                .name("Shavo Odajyan")
                .email("shavoodajyan@mail.com")
                .build();

        User requester1 = UserMapper.dtoToUser(requesterDto1);
        em.persist(requester1);
        requester1.setId(1L);

        User requester2 = UserMapper.dtoToUser(requesterDto2);
        em.persist(requester2);
        requester2.setId(2L);

        User owner1 = UserMapper.dtoToUser(ownerDto1);
        em.persist(owner1);
        owner1.setId(3L);

        User owner2 = UserMapper.dtoToUser(ownerDto2);
        em.persist(owner2);
        owner2.setId(4L);
        em.flush();

        LocalDateTime start = LocalDateTime.now().minusSeconds(1);
        AddItemRequestDto addItemRequestDto1 = new AddItemRequestDto("Описание запроса 1");
        ItemRequest itemRequest1 = ItemRequestMapper.dtoToItemRequest(addItemRequestDto1, requester1);
        em.persist(itemRequest1);

        AddItemRequestDto addItemRequestDto2 = new AddItemRequestDto("Описание запроса 2");
        ItemRequest itemRequest2 = ItemRequestMapper.dtoToItemRequest(addItemRequestDto2, requester1);
        em.persist(itemRequest2);

        AddItemRequestDto addItemRequestDto3 = new AddItemRequestDto("Описание запроса 3");
        ItemRequest itemRequest3 = ItemRequestMapper.dtoToItemRequest(addItemRequestDto3, requester2);
        em.persist(itemRequest3);
        em.flush();

        LocalDateTime end = LocalDateTime.now().plusSeconds(1);

        ItemDto itemDto1 = ItemDto.builder()
                .name("вещь 1")
                .description("описание вещи 1")
                .available(true)
                .requestId(itemRequest1.getId())
                .build();
        Item item1 = ItemMapper.dtoToItem(itemDto1, owner1, itemRequest1);
        em.persist(item1);
        em.flush();
        itemDto1.setId(1L);

        ItemDto itemDto2 = ItemDto.builder()
                .name("вещь 2")
                .description("описание вещи 2")
                .available(false)
                .build();
        Item item2 = ItemMapper.dtoToItem(itemDto2, owner1, null);
        em.persist(item2);
        em.flush();
        itemDto2.setId(2L);

        ItemDto itemDto3 = ItemDto.builder()
                .name("вещь 3")
                .description("описание вещи 3")
                .available(true)
                .requestId(itemRequest2.getId())
                .build();
        Item item3 = ItemMapper.dtoToItem(itemDto3, owner2, itemRequest2);
        em.persist(item3);
        em.flush();
        itemDto3.setId(3L);

        ItemDto itemDto4 = ItemDto.builder()
                .name("вещь 4")
                .description("описание вещи 4")
                .available(true)
                .requestId(itemRequest2.getId())
                .build();
        Item item4 = ItemMapper.dtoToItem(itemDto4, owner1, itemRequest3);
        em.persist(item4);
        em.flush();
        itemDto4.setId(4L);

        List<GetItemRequestDto> savedItemRequestsDto = itemRequestService.findAllByRequesterId(requester1.getId());

        assertThat(savedItemRequestsDto, notNullValue());
        assertThat(savedItemRequestsDto, hasSize(2));
        assertThat(savedItemRequestsDto.get(0).getId(), equalTo(itemRequest2.getId()));
        assertThat(savedItemRequestsDto.get(0).getDescription(), equalTo(itemRequest2.getDescription()));
        assertThat(savedItemRequestsDto.get(0).getCreated(), greaterThan(start));
        assertThat(savedItemRequestsDto.get(0).getCreated(), lessThan(end));
        assertThat(savedItemRequestsDto.get(0).getItems(), hasSize(1));
        assertThat(savedItemRequestsDto.get(0).getItems().get(0), equalTo(itemDto3));
        assertThat(savedItemRequestsDto.get(1).getId(), equalTo(itemRequest1.getId()));
        assertThat(savedItemRequestsDto.get(1).getDescription(), equalTo(itemRequest1.getDescription()));
        assertThat(savedItemRequestsDto.get(1).getCreated(), greaterThan(start));
        assertThat(savedItemRequestsDto.get(1).getCreated(), lessThan(end));
        assertThat(savedItemRequestsDto.get(1).getItems(), hasSize(1));
        assertThat(savedItemRequestsDto.get(1).getItems().get(0), equalTo(itemDto1));
    }

    @Test
    void findAll() {
        UserDto requesterDto1 = UserDto.builder()
                .name("Serj Tankian")
                .email("serjtankian@mail.com")
                .build();
        UserDto requesterDto2 = UserDto.builder()
                .name("John Dolmayan")
                .email("johndolmayan@mail.com")
                .build();
        UserDto ownerDto1 = UserDto.builder()
                .name("Daron Malakian")
                .email("daronmalakian@mail.com")
                .build();
        UserDto ownerDto2 = UserDto.builder()
                .name("Shavo Odajyan")
                .email("shavoodajyan@mail.com")
                .build();

        User requester1 = UserMapper.dtoToUser(requesterDto1);
        em.persist(requester1);
        requester1.setId(1L);

        User requester2 = UserMapper.dtoToUser(requesterDto2);
        em.persist(requester2);
        requester2.setId(2L);

        User owner1 = UserMapper.dtoToUser(ownerDto1);
        em.persist(owner1);
        owner1.setId(3L);

        User owner2 = UserMapper.dtoToUser(ownerDto2);
        em.persist(owner2);
        owner2.setId(4L);
        em.flush();

        LocalDateTime start = LocalDateTime.now().minusSeconds(1);

        AddItemRequestDto addItemRequestDto1 = new AddItemRequestDto("Описание запроса 1");
        ItemRequest itemRequest1 = ItemRequestMapper.dtoToItemRequest(addItemRequestDto1, requester1);
        em.persist(itemRequest1);

        AddItemRequestDto addItemRequestDto2 = new AddItemRequestDto("Описание запроса 2");
        ItemRequest itemRequest2 = ItemRequestMapper.dtoToItemRequest(addItemRequestDto2, requester1);
        em.persist(itemRequest2);

        AddItemRequestDto addItemRequestDto3 = new AddItemRequestDto("Описание запроса 3");
        ItemRequest itemRequest3 = ItemRequestMapper.dtoToItemRequest(addItemRequestDto3, requester2);
        em.persist(itemRequest3);
        em.flush();

        LocalDateTime end = LocalDateTime.now().plusSeconds(1);

        ItemDto itemDto1 = ItemDto.builder()
                .name("вещь 1")
                .description("описание вещи 1")
                .available(true)
                .requestId(itemRequest1.getId())
                .build();
        Item item1 = ItemMapper.dtoToItem(itemDto1, owner1, itemRequest1);
        em.persist(item1);
        em.flush();
        itemDto1.setId(1L);

        ItemDto itemDto2 = ItemDto.builder()
                .name("вещь 2")
                .description("описание вещи 2")
                .available(false)
                .build();
        Item item2 = ItemMapper.dtoToItem(itemDto2, owner1, null);
        em.persist(item2);
        em.flush();
        itemDto2.setId(2L);

        ItemDto itemDto3 = ItemDto.builder()
                .name("вещь 3")
                .description("описание вещи 3")
                .available(true)
                .requestId(itemRequest2.getId())
                .build();
        Item item3 = ItemMapper.dtoToItem(itemDto3, owner2, itemRequest2);
        em.persist(item3);
        em.flush();
        itemDto3.setId(3L);

        ItemDto itemDto4 = ItemDto.builder()
                .name("вещь 4")
                .description("описание вещи 4")
                .available(true)
                .requestId(itemRequest2.getId())
                .build();
        Item item4 = ItemMapper.dtoToItem(itemDto4, owner1, itemRequest3);
        em.persist(item4);
        em.flush();
        itemDto4.setId(4L);

        int from = 0;
        int size = 10;
        List<GetItemRequestDto> savedItemRequestsDto = itemRequestService.findAll(requester2.getId(), from, size);

        assertThat(savedItemRequestsDto, notNullValue());
        assertThat(savedItemRequestsDto, hasSize(2));
        assertThat(savedItemRequestsDto.get(0).getId(), equalTo(itemRequest2.getId()));
        assertThat(savedItemRequestsDto.get(0).getDescription(), equalTo(itemRequest2.getDescription()));
        assertThat(savedItemRequestsDto.get(0).getCreated(), greaterThan(start));
        assertThat(savedItemRequestsDto.get(0).getCreated(), lessThan(end));
        assertThat(savedItemRequestsDto.get(0).getItems(), hasSize(1));
        assertThat(savedItemRequestsDto.get(0).getItems().get(0), equalTo(itemDto3));
        assertThat(savedItemRequestsDto.get(1).getId(), equalTo(itemRequest1.getId()));
        assertThat(savedItemRequestsDto.get(1).getDescription(), equalTo(itemRequest1.getDescription()));
        assertThat(savedItemRequestsDto.get(1).getCreated(), greaterThan(start));
        assertThat(savedItemRequestsDto.get(1).getCreated(), lessThan(end));
        assertThat(savedItemRequestsDto.get(1).getItems(), hasSize(1));
        assertThat(savedItemRequestsDto.get(1).getItems().get(0), equalTo(itemDto1));
    }

    @Test
    void findAll_whenFromGreaterThanZero() {
        UserDto requesterDto1 = UserDto.builder()
                .name("Serj Tankian")
                .email("serjtankian@mail.com")
                .build();
        UserDto requesterDto2 = UserDto.builder()
                .name("John Dolmayan")
                .email("johndolmayan@mail.com")
                .build();
        UserDto ownerDto1 = UserDto.builder()
                .name("Daron Malakian")
                .email("daronmalakian@mail.com")
                .build();
        UserDto ownerDto2 = UserDto.builder()
                .name("Shavo Odajyan")
                .email("shavoodajyan@mail.com")
                .build();

        User requester1 = UserMapper.dtoToUser(requesterDto1);
        em.persist(requester1);
        requester1.setId(1L);

        User requester2 = UserMapper.dtoToUser(requesterDto2);
        em.persist(requester2);
        requester2.setId(2L);

        User owner1 = UserMapper.dtoToUser(ownerDto1);
        em.persist(owner1);
        owner1.setId(3L);

        User owner2 = UserMapper.dtoToUser(ownerDto2);
        em.persist(owner2);
        owner2.setId(4L);
        em.flush();

        LocalDateTime start = LocalDateTime.now().minusSeconds(1);

        AddItemRequestDto addItemRequestDto1 = new AddItemRequestDto("Описание запроса 1");
        ItemRequest itemRequest1 = ItemRequestMapper.dtoToItemRequest(addItemRequestDto1, requester1);
        em.persist(itemRequest1);

        AddItemRequestDto addItemRequestDto2 = new AddItemRequestDto("Описание запроса 2");
        ItemRequest itemRequest2 = ItemRequestMapper.dtoToItemRequest(addItemRequestDto2, requester1);
        em.persist(itemRequest2);

        AddItemRequestDto addItemRequestDto3 = new AddItemRequestDto("Описание запроса 3");
        ItemRequest itemRequest3 = ItemRequestMapper.dtoToItemRequest(addItemRequestDto3, requester2);
        em.persist(itemRequest3);
        em.flush();

        LocalDateTime end = LocalDateTime.now().plusSeconds(1);

        ItemDto itemDto1 = ItemDto.builder()
                .name("вещь 1")
                .description("описание вещи 1")
                .available(true)
                .requestId(itemRequest1.getId())
                .build();
        Item item1 = ItemMapper.dtoToItem(itemDto1, owner1, itemRequest1);
        em.persist(item1);
        em.flush();
        itemDto1.setId(1L);

        ItemDto itemDto2 = ItemDto.builder()
                .name("вещь 2")
                .description("описание вещи 2")
                .available(false)
                .build();
        Item item2 = ItemMapper.dtoToItem(itemDto2, owner1, null);
        em.persist(item2);
        em.flush();
        itemDto2.setId(2L);

        ItemDto itemDto3 = ItemDto.builder()
                .name("вещь 3")
                .description("описание вещи 3")
                .available(true)
                .requestId(itemRequest2.getId())
                .build();
        Item item3 = ItemMapper.dtoToItem(itemDto3, owner2, itemRequest2);
        em.persist(item3);
        em.flush();
        itemDto3.setId(3L);

        ItemDto itemDto4 = ItemDto.builder()
                .name("вещь 4")
                .description("описание вещи 4")
                .available(true)
                .requestId(itemRequest2.getId())
                .build();
        Item item4 = ItemMapper.dtoToItem(itemDto4, owner1, itemRequest3);
        em.persist(item4);
        em.flush();
        itemDto4.setId(4L);

        int from = 1;
        int size = 1;
        List<GetItemRequestDto> savedItemRequestsDto = itemRequestService.findAll(requester2.getId(), from, size);

        assertThat(savedItemRequestsDto, notNullValue());
        assertThat(savedItemRequestsDto, hasSize(1));
        assertThat(savedItemRequestsDto.get(0).getId(), equalTo(itemRequest1.getId()));
        assertThat(savedItemRequestsDto.get(0).getDescription(), equalTo(itemRequest1.getDescription()));
        assertThat(savedItemRequestsDto.get(0).getCreated(), greaterThan(start));
        assertThat(savedItemRequestsDto.get(0).getCreated(), lessThan(end));
        assertThat(savedItemRequestsDto.get(0).getItems(), hasSize(1));
        assertThat(savedItemRequestsDto.get(0).getItems().get(0), equalTo(itemDto1));
    }

    @Test
    void getById() {
        UserDto requesterDto = UserDto.builder()
                .name("Serj Tankian")
                .email("serjtankian@mail.com")
                .build();
        UserDto ownerDto = UserDto.builder()
                .name("John Dolmayan")
                .email("johndolmayan@mail.com")
                .build();

        User requester = UserMapper.dtoToUser(requesterDto);
        em.persist(requester);
        User owner = UserMapper.dtoToUser(ownerDto);
        em.persist(owner);
        em.flush();

        LocalDateTime start = LocalDateTime.now().minusSeconds(1);
        ;
        AddItemRequestDto addItemRequestDto = new AddItemRequestDto("Описание запроса");
        ItemRequest itemRequest = ItemRequestMapper.dtoToItemRequest(addItemRequestDto, requester);
        em.persist(itemRequest);
        em.flush();
        LocalDateTime end = LocalDateTime.now().plusSeconds(1);
        ;

        ItemDto itemDto = ItemDto.builder()
                .name("вещь")
                .description("описание вещи")
                .available(true)
                .requestId(itemRequest.getId())
                .build();
        Item item = ItemMapper.dtoToItem(itemDto, owner, itemRequest);
        em.persist(item);
        em.flush();
        itemDto.setId(1L);

        GetItemRequestDto savedItemRequestDto = itemRequestService.getById(itemRequest.getId(), requester.getId());

        assertThat(savedItemRequestDto, notNullValue());
        assertThat(savedItemRequestDto.getId(), equalTo(itemRequest.getId()));
        assertThat(savedItemRequestDto.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(savedItemRequestDto.getCreated(), greaterThan(start));
        assertThat(savedItemRequestDto.getCreated(), lessThan(end));
        assertThat(savedItemRequestDto.getItems(), hasSize(1));
        assertThat(savedItemRequestDto.getItems().get(0), equalTo(itemDto));
    }
}