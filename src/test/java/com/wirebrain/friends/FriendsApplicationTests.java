package com.wirebrain.friends;

import com.wirebrain.friends.controller.FriendController;
import com.wirebrain.friends.model.Friend;
import com.wirebrain.friends.service.FriendService;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.xml.bind.ValidationException;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FriendsApplicationTests {

    @LocalServerPort
    int randomServerPort;

    @Autowired
    FriendController friendController;

    @Autowired
    FriendService friendService;

    @Test
    public void testContextLoads() {
        Assert.assertNotNull(friendController);
    }

    @Test
    public void testCreateReadDeletionSystem() {
        RestTemplate restTemplate = new RestTemplate();

        String url = "http://localhost:" + randomServerPort + "/friend";

        Friend friend = new Friend("Gordon", "Moore");
        ResponseEntity<Friend> entity = restTemplate.postForEntity(url, friend, Friend.class);

        Friend[] friends = restTemplate.getForObject(url, Friend[].class);
        Assertions.assertThat(friends).extracting(Friend::getFirstName).contains("Gordon");

        restTemplate.delete(url + "/" + entity.getBody().getId());

        Friend[] friendsAfterDelete = restTemplate.getForObject(url, Friend[].class);
        Assertions.assertThat(friendsAfterDelete).extracting(Friend::getFirstName).doesNotContain("Gordon");
    }

    @Test
    public void testCreateReadDeleteIntegration() {
        Friend friend = new Friend("Gordon", "Moore");
        Friend friendResult = friendController.create(friend);

        Iterable<Friend> friends = friendController.read();
        Assertions.assertThat(friends).first().hasFieldOrPropertyWithValue("firstName", "Gordon");

        friendController.delete(friendResult.getId());
        Assertions.assertThat(friendController.read()).isEmpty();
    }

    @Test
    public void testCreateReadDeleteService() {
        Friend friend = new Friend("Gordon", "Moore");

        friendService.save(friend);

        Iterable<Friend> friends = friendService.findAll();
        Assertions.assertThat(friends).extracting(Friend::getFirstName).containsOnly("Gordon");

        friendService.deleteAll();
        Assertions.assertThat(friendService.findAll()).isEmpty();
    }

    @Test(expected = ValidationException.class)
    public void errorHandlingValidationExceptionThrow() throws ValidationException {
        friendController.somethingIsWrong();
    }

    @Test
    public void testErrorHandlingReturnsBadRequest(){
        RestTemplate restTemplate = new RestTemplate();

        String url = "http://localhost:" + randomServerPort + "/wrong";

        try {
            restTemplate.getForEntity(url, String.class);
        } catch (HttpClientErrorException e) {
            Assert.assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
        }
    }
}

