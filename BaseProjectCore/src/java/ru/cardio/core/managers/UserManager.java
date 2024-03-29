package ru.cardio.core.managers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import ru.cardio.core.jpa.entity.CardioSession;
import ru.cardio.core.jpa.entity.FriendRequest;
import ru.cardio.core.jpa.entity.User;
import ru.cardio.core.jpa.entity.UserCard;
import ru.cardio.core.utils.UserUtils;
import ru.cardio.exceptions.CardioException;
import ru.cardio.helpers.UserHelper;
import ru.cardio.json.additionals.ResponseConstants;
import ru.cardio.json.entity.SimpleUser;

/**
 *
 * @author rogvold
 */
@Stateless
public class UserManager implements UserManagerLocal {

    @PersistenceContext(unitName = "BaseProjectCorePU")
    EntityManager em;
    @EJB
    UserCardManagerLocal cardMan;

    @Override
    public User getUserById(Long userId) {
        User user;
        if (userId == null) {
            return null;
        }
        user = em.find(User.class, userId);
        return user;
    }

    @Override
    public User getUserByEmail(String email) throws CardioException {
        checkEmail(email);
        Query q = em.createQuery("select u from User u where u.email=:email").setParameter("email", email);
        try {
            return (User) q.getSingleResult();
        } catch (Exception e) {
            System.out.println("getUserByEmail: e = " + e.toString());
            return null;
        }
    }

    private void checkEmail(String email) throws CardioException {
        if (!UserUtils.isValidEmail(email)) {
            throw new CardioException("email '" + email + "' is invalid");
        }
    }

    private void checkUserExistence(String email) throws CardioException {
//        checkEmail(email);
        if (!userExistsByEmail(email)) {
            throw new CardioException("User with email '" + email + "' does not exist");
        }
    }

    @Override
    public User registerNewUser(String email, String password, String firstName, String lastName, int group) throws CardioException {
        checkEmail(email);
        if (userExistsByEmail(email)) {
            throw new CardioException("User with email '" + email + "' is already registered in the system");
        }

        User u = new User();
        u.setUserInfoUpdateCount(1);
        u.setEmail(email);
        u.setPassword(password);
        u.setUserGroup(group);
        u.setFirstName(firstName);
        u.setLastName(lastName);
        return em.merge(u);
    }

    @Override
    public User logInByEmail(String email, String password) throws CardioException {
        User u = getUserByEmail(email);
        if (u.getPassword().equals(password)) {
            return u;
        } else {
            return null;
        }
    }

    @Override
    public boolean userExistsByEmail(String email) throws CardioException {
        return (getUserByEmail(email) == null) ? false : true;
    }

    @Override
    public boolean userExistsById(Long userId) {
        return (getUserById(userId) == null) ? false : true;
    }

    @Override
    public Long getLastCardioSessionId(Long userId) {
        Query q = em.createQuery("select cs from CardioSession cs where (cs.userId = :userId ) and (cs.status = :status)").setParameter("userId", userId).setParameter("status", CardioSession.STATUS_CURRENT);
        try {
            return ((CardioSession) q.getSingleResult()).getId();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean checkAuData(Long userId, String password) {
        System.out.println("checkAuData: userId = " + userId + " / password = " + password);

        User u = em.find(User.class, userId);
        try {
            return u.getPassword().equals(password);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean checkAuthorisationData(String email, String password) throws CardioException {
        User u;
        u = getUserByEmail(email);
        try {
            return u.getPassword().equals(password);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean checkEmailAndPassword(String email, String password) throws CardioException {
        User u = getUserByEmail(email);
        if (u == null) {
            throw new CardioException("user with email '" + email + "' is not found");
        }
        if (u.getPassword().equals(password)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<User> getAllUsersByRole(int userGroup) {
        String jpql = "select u from User u where u.userGroup = :userGroup";
        Query q = em.createQuery(jpql).setParameter("userGroup", userGroup);
        return q.getResultList();
    }

    private void checkPair(User trainer, User trainee) throws Exception {
        if (trainee.getUserGroup() != User.USER) {
            throw new Exception("user (traineeId=" + trainee.getId() + ") is not a trainee");
        }
        if (trainer.getUserGroup() != User.TRAINER) {
            throw new Exception("user (trainerId=" + trainer.getId() + ") is not a trainer");
        }
    }

    private FriendRequest findFriendRequest(Long fromId, Long toId) throws Exception {
        String jpql = "select fr from FriendRequest fr where ( fr.fromId = :fromId) and (fr.toId = :toId)";
        Query q = em.createQuery(jpql).setParameter("fromId", fromId).setParameter("toId", toId);
        try {
            return (FriendRequest) q.getSingleResult();
        } catch (Exception e) {
            throw new Exception("there is no such friend request with fromId = " + fromId + " and toId = " + toId);
        }
    }

    private FriendRequest findFriendRequestWithoutException(Long fromId, Long toId) {
        try {
            return findFriendRequest(fromId, toId);
        } catch (Exception e) {
            System.out.println("connection " + fromId + " - " + toId + " is not found");
            return null;
        }
    }

    private void deleteFriendRequest(FriendRequest fr) {
        if (fr == null) {
            return;
        }
        try {
            em.remove(fr);
        } catch (Exception e) {
        }
    }

    private void deleteFriendConnection(Long userAId, Long userBId) {
        FriendRequest fr1 = findFriendRequestWithoutException(userAId, userBId);
        FriendRequest fr2 = findFriendRequestWithoutException(userBId, userAId);
        deleteFriendRequest(fr1);
        deleteFriendRequest(fr2);
    }

    @Override
    public void bidFriendRequest(Long myId, Long friendId, String message) {
        System.out.println("try to bid " + myId + " to " + friendId);
        if (!checkIfCanBid(myId, friendId)) {
            System.out.println("cannot bid " + myId + " to " + friendId);
            return;
        }
        deleteFriendConnection(myId, friendId);

        FriendRequest fr = new FriendRequest();
        fr.setFromId(myId);
        fr.setToId(friendId);
        fr.setStatusChangeMessage(message);
        System.out.println("merging....");
        em.merge(fr);
    }

    @Override
    public void confirmFriendRequest(Long myId, Long friendId) throws Exception {
        FriendRequest frReq = findFriendRequest(friendId, myId);
        frReq.setStatus(FriendRequest.CONFIRMED);
        em.merge(frReq);
    }

    @Override
    public void rejectFriendRequest(Long myId, Long friendId, String rejectionMessage) throws Exception {
        FriendRequest frReq = findFriendRequest(friendId, myId);
        frReq.setStatus(FriendRequest.REJECTED);
        frReq.setStatusChangeMessage(rejectionMessage);
        em.merge(frReq);
    }

    @Override
    public void rejectFriendRequest(Long myId, Long friendId) throws Exception {
        FriendRequest frReq = findFriendRequest(friendId, myId);
        frReq.setStatus(FriendRequest.CONFIRMED);
        em.merge(frReq);
    }

    @Override
    public int getUsersFriendshipStatus(Long userA, Long userB) {

        FriendRequest fr, fr1, fr2;
        fr1 = findFriendRequestWithoutException(userA, userB);
        fr2 = findFriendRequestWithoutException(userB, userA);
        if ((fr1 == null) && (fr2 == null)) {
            return User.FRIENDSHIP_UNFAMILIAR;
        }

        if (((fr1 != null) && (fr1.getStatus() == FriendRequest.CONFIRMED)) || ((fr2 != null) && (fr2.getStatus() == FriendRequest.CONFIRMED))) {
            return User.FRIENDSHIP_CONFIRMED;
        }

        if ((fr1 != null) && fr1.getStatus() == FriendRequest.NEW) {
            return User.FRIENDSHIP_FIRST_REQUESTED_SECOND;
        }
        if ((fr2 != null) && fr2.getStatus() == FriendRequest.NEW) {
            return User.FRIENDSHIP_SECOND_REQUESTED_FIRST;
        }


        if ((fr1 != null) && fr1.getStatus() == FriendRequest.REJECTED) {
            return User.FRIENDSHIP_REJECTED_BY_SECOND;
        }
        if ((fr2 != null) && fr2.getStatus() == FriendRequest.REJECTED) {
            return User.FRIENDSHIP_REJECTED_BY_FIRST;
        }


        return 0;
    }

    @Override
    public List<User> getNewFriends(Long myId) {
        Query q = em.createNamedQuery("getBidsToMe").setParameter("userId", myId).setParameter("status", FriendRequest.NEW);
        return q.getResultList();
    }

    @Override
    public boolean checkIfCanBid(Long myId, Long friendId) {
        User first = getUserById(myId);
        User second = getUserById(friendId);

        try {
            if (first.getUserGroup() == second.getUserGroup()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        int status = getUsersFriendshipStatus(myId, friendId);
        System.out.println("checkIfCanBid: myId = " + myId + "; friendId = " + friendId + " status = " + status);
        if (status == User.FRIENDSHIP_FIRST_REQUESTED_SECOND || status == User.FRIENDSHIP_SECOND_REQUESTED_FIRST) {
            return false;
        }
        return true;
    }

    @Override
    public List<User> getFriends(Long userId) {
        return em.createNamedQuery("getFriends").setParameter("userId", userId).getResultList();
    }

    @Override
    public boolean areConnected(Long userAId, Long userBId) {
        if (userAId == null || userBId == null) {
            return false;
        }
        int st = getUsersFriendshipStatus(userAId, userBId);
        if (st == User.FRIENDSHIP_CONFIRMED) {
            return true;
        }

        return false;

    }

    @Override
    public List<User> getUsersExceptFor(int role, Object... etrainers) {
        List<User> e2trainers = new ArrayList();

        for (int i = 0; i < etrainers.length; i++) {
            List<User> cList = (List<User>) etrainers[i];
            e2trainers.addAll(cList);
        }
        List<User> allTrainers = getAllUsersByRole(role);
        List<User> list = new ArrayList();
        for (User t : allTrainers) {
            if (!e2trainers.contains(t)) {
                list.add(t);
            }
        }
        return list;
    }

    @Override
    public Integer getFriendsAmount(Long userId) {
        Long num = (Long) em.createNamedQuery("getFriendsAmount").setParameter("userId", userId).getSingleResult();
        return Integer.parseInt(num.toString());
    }

    @Override
    public void deleteFriend(Long userAId, Long userBId) {
        deleteFriendConnection(userAId, userBId);
    }

    @Override
    public int getUserGroupById(Long userId) {
        User u = getUserById(userId);
        if (u == null) {
            return -1;
        }
        return u.getUserGroup();
    }

    @Override
    public boolean hasNewFriends(Long userId) {
        return (newFriendsAmount(userId) > 0);
    }

    @Override
    public int newFriendsAmount(Long userId) {
        Long num = (Long) em.createNamedQuery("getBidsToMeAmount").setParameter("userId", userId).setParameter("status", FriendRequest.NEW).getSingleResult();
        return Integer.parseInt(num.toString());
    }

    @Override
    public void updateInfo(Long userId, String fName, String lName, String dep, String newStatusMessage) {
        User user = getUserById(userId);
        if (user == null) {
            return;
        }
        if (newStatusMessage != null) {
            user.setStatusMessage(newStatusMessage);
            user.setStatusMessageChangingDate(new Date());
        }
        if (dep != null) {
            user.setDepartment(dep);
        }
        user.setFirstName(fName);
        user.setLastName(lName);
        user.setUserInfoUpdateCount(user.getUserInfoUpdateCount() == null ? 1 : user.getUserInfoUpdateCount() + 1);

        em.merge(user);
    }

    @Override
    public boolean userSensorIsWorking(Long userId) {
        if (userId == null) {
            return false;
        }
        try {
            Date now = new Date();
            User user = getUserById(userId);
            if (now.getTime() - user.getLastDataRecievedDate().getTime() < CardioSession.USER_ACTIVE_INTERVAL) {
                return true;
            }
        } catch (Exception e) {
        }

        return false;

    }

    private void checkInteger(Integer i) throws CardioException {
        if (i == null) {
            return;
        }
        if (i < 0) {
            throw new CardioException(i + " < 0");
        }
    }

    private void checkFloat(Float i) throws CardioException {
        if (i == null) {
            return;
        }
        if (i < 0) {
            throw new CardioException(i + " < 0");
        }
    }

    private void checkSex(Integer sex) throws CardioException {
        if (sex == null) {
            return;
        }
        if (!((sex.equals(0)) || (sex.equals(1)))) {
            throw new CardioException("sex can be equal to 1 or 0");
        }
    }

    private void checkSimpleUser(SimpleUser su) throws CardioException {
        checkInteger(su.getAge());
        checkSex(su.getSex());
        checkFloat(su.getWeight());
        checkFloat(su.getHeight());
    }

    @Override
    public void updateInfo(SimpleUser su) throws CardioException {
//        if (!checkEmailAndPassword(su.getEmail(), su.getPassword())) {
//            throw new CardioException("incorrect pair email/password");
//        }
        User u = getUserByEmail(su.getEmail());
        updateInfo(u.getId(), su);
//
//        u.setFirstName(su.getFirstName());
//        u.setLastName(su.getLastName());
//        u.setDepartment(su.getDepartment());
//        u.setStatusMessage(su.getStatusMessage());
//        u = em.merge(u);
//
//
//        UserCard uc = cardMan.getCardByUserId(u.getId());
//        uc.setAboutMe(su.getAbout());
//        uc.setDescription(su.getDescription());
//        uc.setDiagnosis(su.getDiagnosis());
//        uc.setFirstName(su.getFirstName());
//        uc.setLastName(su.getLastName());
//
//        System.out.println("su.getSex() = " + su.getSex());
//
//        uc.setSex(su.getSex());
//
//        uc.setWeight(su.getWeight());
//        uc.setHeight(su.getHeight());
//        uc.setAge(su.getAge());
//        uc.setBirthDate(su.getBirthDate());
//
//        em.merge(uc);
    }

    @Override
    public void updateInfo(Long userId, SimpleUser su) {
        User u = getUserById(userId);

        u.setFirstName(su.getFirstName());
        u.setLastName(su.getLastName());
        u.setDepartment(su.getDepartment());
        u.setStatusMessage(su.getStatusMessage());

        u.setUserInfoUpdateCount(u.getUserInfoUpdateCount() == null ? 1 : u.getUserInfoUpdateCount() + 1);

        u = em.merge(u);


        UserCard uc = cardMan.getCardByUserId(u.getId());
        uc.setAboutMe(su.getAbout());
        uc.setDescription(su.getDescription());
        uc.setDiagnosis(su.getDiagnosis());
        uc.setFirstName(su.getFirstName());
        uc.setLastName(su.getLastName());

        System.out.println("su.getSex() = " + su.getSex());

        uc.setSex(su.getSex());

        uc.setWeight(su.getWeight());
        uc.setHeight(su.getHeight());
        uc.setAge(su.getAge());
        uc.setBirthDate(su.getBirthDate());

        em.merge(uc);
    }

    @Override
    public SimpleUser getSimpleInfo(String email, String password) throws CardioException {
        if (!checkEmailAndPassword(email, password)) {
            throw new CardioException("incorrect pair email/password");
        }
        User u = getUserByEmail(email);
        UserCard uc = cardMan.getCardByUserId(u.getId());
        SimpleUser su = new SimpleUser(u.getEmail(), u.getPassword(), u.getFirstName(), u.getLastName(), u.getDepartment(), uc.getAboutMe(), uc.getDiagnosis(), uc.getDescription(), u.getStatusMessage(), uc.getWeight(), uc.getHeight(), uc.getSex(), uc.getAge(), uc.getBirthDate(), u.getId(), uc.getPhone());
        return su;
    }

    @Override
    public SimpleUser getSimpleInfo(Long userId) throws CardioException {
        User u = getUserById(userId);
        UserCard uc = cardMan.getCardByUserId(u.getId());
        SimpleUser su = new SimpleUser(u.getEmail(), u.getPassword(), u.getFirstName(), u.getLastName(), u.getDepartment(), uc.getAboutMe(), uc.getDiagnosis(), uc.getDescription(), u.getStatusMessage(), uc.getWeight(), uc.getHeight(), uc.getSex(), uc.getAge(), uc.getBirthDate(), u.getId(), uc.getPhone());
        return su;
    }

    @Override
    public void checkAuthInfo(String email, String password) throws CardioException {
        Query q = em.createQuery("select u from User u where u.email=:email and u.password = :password").setParameter("email", email).setParameter("password", password);
        List<User> ul = q.getResultList();
        if ((ul == null) || (ul.isEmpty())) {
            System.out.println("incorrect pair");
            throw new CardioException("Incorrect pair email/password");
        }

//        boolean b = checkEmailAndPassword(email, password);
//        if (!b) {
//            throw new CardioException("Incorrect pair email/password");
//        }
    }

    @Override
    public Long getUserAvatar(Long userId) {
        User u = getUserById(userId);
        return u.getAvatarId();
    }

    @Override
    public void updateAvatar(Long userId, Long avatarId) throws CardioException {
        System.out.println("updateing avatar: aId = " + avatarId);
        User user = getUserById(userId);
        user.setAvatarId(avatarId);
        em.merge(user);
    }

    @Override
    public boolean isActive(Long userId) throws CardioException {
        User u = getUserById(userId);
        if (u == null) {
            throw new CardioException("userId is null");
        }
        if ((u.getLastDataRecievedDate() != null) && ((new Date()).getTime() - u.getLastDataRecievedDate().getTime() <= CardioSessionManager.IS_USER_ACTIVE_TIMEOUT)) {
            return true;
        }
        return false;
    }

    @Override
    public Integer getUserInfoVersion(Long userId) throws CardioException {
        User u = getUserById(userId);
        if (u == null) {
            throw new CardioException("getUserInfoVersion: user is null");
        }
        return u.getUserInfoUpdateCount();
    }
}
