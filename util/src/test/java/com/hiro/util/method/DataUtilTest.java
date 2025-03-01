package com.hiro.util.method;

import com.hiro.util.methods.DataUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
public class DataUtilTest {

    static List<Member> getMembers() {
        return List.of(
                new Member("Harry", 11, "0900000001", "001@gmail.com",
                        "01", "Han",LocalDateTime.now()),
                new Member("Jane", 12, "0900000002", "002@gmail.com",
                        "02", "JJ",LocalDateTime.now()),
                new Member("YPMen", 13, "0900000003", "003@gmail.com",
                        "03", "Yeah",LocalDateTime.now()),
                new Member("GoShare", 14, "0900000004", "004@gmail.com",
                        "04", "Go",LocalDateTime.now()),
                new Member("Jack", 15, "0900000005", "005@gmail.com",
                        "05", "jakie",LocalDateTime.now())
        );
    }

    static List<Client> getClients() {
        return List.of(
                new Client("Harry", 91, "0999999991", "101@gmail.com", LocalDateTime.now()),
                new Client("Jane", 92, "0999999992", "102@gmail.com", LocalDateTime.now()),
                new Client("Hook", 93, "0999999993", "103@gmail.com", LocalDateTime.now())

        );
    }

    @Test
    void convert() {
        var clients = getClients();
        for (var client : clients) {
            log.info("convert client: {}", client);
            var member = DataUtil.convert(client, Member.class);
            log.info("get member: {}", member);
            assertNotNull(member);
            assertEquals(member, new Member(client.getName(), client.getAge(), client.getMobile(), client.getEmail(),
                    null, null, null));
        }

        var members = getMembers();
        for (var member : members) {
            log.info("convert member: {}", member);
            var client = DataUtil.convert(member, Client.class);
            log.info("get client: {}", client);
            assertNotNull(client);
            assertEquals(client, new Client(member.getName(), member.getAge(), member.getMobile(), member.getEmail(), null));
        }
    }

    @Test
    void flush() {
        var clients = getClients();
        var members = getMembers();
        for (var client : clients) {
            for (var member : members) {
                if (client.getName().equals(member.getName())) {
                    log.info("flush client to member: {}, {}", client, member);

                    var reference = DataUtil.convert(member, Member.class);
                    assertNotNull(reference);

                    reference.setAge(client.getAge());
                    reference.setMobile(client.getMobile());
                    reference.setEmail(client.getEmail());
                    log.info("get reference: {}", reference);

                    var result = DataUtil.flush(client, member);
                    log.info("flushed member: {}", result);
                    assertEquals(reference, result);
                }
            }
        }
    }

    @Test
    void flushIn() {
        var clients = getClients();
        var members = getMembers();
        var set = Set.of("age", "mobile");
        for (var client : clients) {
            for (var member : members) {
                if (client.getName().equals(member.getName())) {
                    log.info("flush client to member with set: {}, {}, {}", client, member, set);

                    var reference = DataUtil.convert(member, Member.class);
                    assertNotNull(reference);

                    reference.setAge(client.getAge());
                    reference.setMobile(client.getMobile());
                    log.info("get reference of set: {}", reference);

                    var result = DataUtil.flushIn(client, member, set);
                    log.info("flushed member with set: {}", result);
                    assertEquals(reference, result);
                }
            }
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Client {

        /**
         * Common
         */
        private String name;
        private int age;
        private String mobile;
        private String email;

        /**
         * private
         */
        private LocalDateTime lastVisit;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Client client = (Client) o;
            return Objects.equals(name, client.getName()) &&
                    age == client.getAge() &&
                    Objects.equals(mobile, client.getMobile()) &&
                    Objects.equals(email, client.getEmail()) &&
                    Objects.equals(lastVisit, client.getLastVisit());
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, age, mobile, email, lastVisit);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Member {

        /**
         * Common
         */
        private String name;
        private int age;
        private String mobile;
        private String email;

        /**
         * private
         */
        private String memberNumber;
        private String nickName;
        private LocalDateTime applyDate;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Member member = (Member) o;
            return age == member.age &&
                    Objects.equals(name, member.name) &&
                    Objects.equals(mobile, member.mobile) &&
                    Objects.equals(email, member.email) &&
                    Objects.equals(memberNumber, member.memberNumber) &&
                    Objects.equals(nickName, member.nickName) &&
                    Objects.equals(applyDate, member.applyDate);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, age, mobile, email, memberNumber, nickName, applyDate);
        }
    }
}
