package com.wirebrain.friends;

import com.wirebrain.friends.model.Friend;
import org.assertj.core.api.Assertions;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class SystemTests {

    public static void testCreateReadDelete( int randomServerPort){
        RestTemplate restTemplate = new RestTemplate();

        String url = "http://localhost:" + randomServerPort + "/friend";

        Friend friend = new Friend("Gordon","Moore");
        ResponseEntity<Friend> entity = restTemplate.postForEntity(url,friend,Friend.class);

        Friend[] friends = restTemplate.getForObject(url,Friend[].class);
        Assertions.assertThat(friends).extracting(Friend::getFirstName).contains("Gordon");

        restTemplate.delete(url +"/" + entity.getBody().getId());

        Friend[] friendsAfterDelete = restTemplate.getForObject(url,Friend[].class);
        Assertions.assertThat(friendsAfterDelete).extracting(Friend::getFirstName).doesNotContain("Gordon");
    }
}
