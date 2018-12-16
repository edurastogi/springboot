package com.wirebrain.friends;

import com.wirebrain.friends.controller.FriendController;
import com.wirebrain.friends.model.Friend;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FriendsApplicationTests {

	@LocalServerPort
	int randomServerPort;

	@Autowired
	FriendController friendController;

	@Test
	public void contextLoads() {
		Assert.assertNotNull(friendController);
	}

	@Test
	public void testCreateReadDelete(){
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

