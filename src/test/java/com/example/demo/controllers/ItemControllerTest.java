package com.example.demo.controllers;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.TestUtils;
import com.example.demo.model.requests.CreateItemRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {


    private ItemController itemController;

    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    // test happy path for get item by id

    @Test
    public void get_item_by_id_happy_path() {
        Item item = new Item();
        item.setId(1L);
        item.setName("testItem");
        item.setPrice(BigDecimal.valueOf(1.99));
        item.setDescription("testDescription");

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ResponseEntity<Item> response = itemController.getItemById(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Item itemResponse = response.getBody();
        assertNotNull(itemResponse);
        assertEquals(Optional.ofNullable(1L), Optional.ofNullable(itemResponse.getId()));
        assertEquals("testItem", itemResponse.getName());
        assertEquals(BigDecimal.valueOf(1.99), itemResponse.getPrice());
        assertEquals("testDescription", itemResponse.getDescription());
    }

    /**@Test
    public void get_item_by_id_happy_pathv2() {
        Item item = new Item();
        item.setName("doritos");
        item.setPrice(BigDecimal.valueOf(1.99));
        item.setDescription("big size");

        // create item using the item controller
        CreateItemRequest createItemRequest = new CreateItemRequest();
        createItemRequest.setName("doritos");
        createItemRequest.setPrice(BigDecimal.valueOf(1.99));
        createItemRequest.setDescription("big size");

        ResponseEntity<Item> response1 = itemController.createItem(createItemRequest);

        // print out response
        System.out.println(response1);
        //print out item
        System.out.println(response1.getBody().getId());
        System.out.println(response1.getBody().getName());
        System.out.println(response1.getBody().getPrice());
        System.out.println(response1.getBody().getDescription());


        ResponseEntity<Item> response2 = itemController.getItemById(Long.valueOf(1));

        // print out response
        System.out.println(response2);

    }
     **/

    // test unhappy path for get item by id, item not found

    @Test
    public void get_item_by_id_not_found() {

        ResponseEntity<Item> response = itemController.getItemById(1L);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    // test happy path for get items by name

    @Test
    public void get_items_by_name_happy_path() {
        Item item = new Item();
        item.setId(1L);
        item.setName("testItem");
        item.setPrice(BigDecimal.valueOf(1.99));
        item.setDescription("testDescription");

        when(itemRepository.findByName("testItem")).thenReturn(java.util.Collections.singletonList(item));

        ResponseEntity<java.util.List<Item>> response = itemController.getItemsByName("testItem");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        java.util.List<Item> items = response.getBody();
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(Optional.ofNullable(1L), Optional.ofNullable(items.get(0).getId()));
        assertEquals("testItem", items.get(0).getName());
        assertEquals(BigDecimal.valueOf(1.99), items.get(0).getPrice());
        assertEquals("testDescription", items.get(0).getDescription());
    }

    // test unhappy path for get items by name, item not found

    @Test
    public void get_items_by_name_not_found() {

        ResponseEntity<java.util.List<Item>> response = itemController.getItemsByName("testItem");

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }


}
