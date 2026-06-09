package contactapp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * ContactService now integrates with SQLite through ContactDAO.
 * The in-memory list + map acts as a synchronized cache.
 */
public class ContactService {

    private static final Logger logger = Logger.getLogger(ContactService.class.getName());

    private final ContactDAO dao = new ContactDAO();

    // Cache for fast lookup and sorting 
    private ArrayList<Contact> contacts = new ArrayList<>();
    private Map<String, Contact> contactMap = new HashMap<>();

    public ContactService() {
        Database.initialize();
        loadCacheFromDatabase();
    }

    /** Loads all contacts from the database into memory. */
    private void loadCacheFromDatabase() {
        contacts = new ArrayList<>(dao.getAllContacts());
        contactMap.clear();
        for (Contact c : contacts) {
            contactMap.put(c.getContactId(), c);
        }
    }

    /** O(1) lookup */
    public Contact findContactById(String id) {
        return contactMap.get(id);
    }

    /** Add contact → write to DB → refresh cache */
    public void addContact(String firstName, String lastName, String phone, String address) {
        String id = IdGenerator.generateId();
        logger.info("Creating new contact with ID: " + id);

        Contact newContact = new Contact(id, firstName, lastName, phone, address);
        dao.insertContact(newContact);

        loadCacheFromDatabase();
        logger.info("Contact added successfully: " + id);
    }

    /** Delete contact → DB → refresh cache */
    public void deleteContact(String contactId) {
        logger.info("Attempting to delete contact with ID: " + contactId);

        dao.deleteContact(contactId);
        loadCacheFromDatabase();

        logger.info("Delete operation completed for ID: " + contactId);
    }

    /** Update operations, but now write to DB */
    public void updateFirstName(String contactId, String firstName) {
        Contact contact = findContactById(contactId);
        if (contact == null) {
            logger.warning("Update failed — no contact found with ID: " + contactId);
            return;
        }
        contact.setFirstName(firstName);
        dao.updateContact(contact);
        loadCacheFromDatabase();
    }

    public void updateLastName(String contactId, String lastName) {
        Contact contact = findContactById(contactId);
        if (contact == null) {
            logger.warning("Update failed — no contact found with ID: " + contactId);
            return;
        }
        contact.setLastName(lastName);
        dao.updateContact(contact);
        loadCacheFromDatabase();
    }

    public void updatePhone(String contactId, String phone) {
        Contact contact = findContactById(contactId);
        if (contact == null) {
            logger.warning("Update failed — no contact found with ID: " + contactId);
            return;
        }
        contact.setPhone(phone);
        dao.updateContact(contact);
        loadCacheFromDatabase();
    }

    public void updateAddress(String contactId, String address) {
        Contact contact = findContactById(contactId);
        if (contact == null) {
            logger.warning("Update failed — no contact found with ID: " + contactId);
            return;
        }
        contact.setAddress(address);
        dao.updateContact(contact);
        loadCacheFromDatabase();
    }

    /** Sorting algorithms from Enhancement 2 */
    public void sortByFirstName() {
        contacts.sort((a, b) -> a.getFirstName().compareToIgnoreCase(b.getFirstName()));
    }

    public void sortByLastName() {
        contacts.sort((a, b) -> a.getLastName().compareToIgnoreCase(b.getLastName()));
    }

    public void sortById() {
        contacts.sort((a, b) -> a.getContactId().compareTo(b.getContactId()));
    }

    /** Searching algorithms */
    public Contact findContactByPhone(String phone) {
        for (Contact c : contacts) {
            if (c.getPhone().equals(phone)) {
                return c;
            }
        }
        return null;
    }

    public List<Contact> findContactsByLastName(String lastName) {
        List<Contact> results = new ArrayList<>();
        for (Contact c : contacts) {
            if (c.getLastName().equalsIgnoreCase(lastName)) {
                results.add(c);
            }
        }
        return results;
    }

    public List<Contact> searchByPartialName(String partial) {
        List<Contact> results = new ArrayList<>();
        String lower = partial.toLowerCase();

        for (Contact c : contacts) {
            if (c.getFirstName().toLowerCase().contains(lower) ||
                c.getLastName().toLowerCase().contains(lower)) {
                results.add(c);
            }
        }
        return results;
    }

    /** Returns the in-memory cache */
    public ArrayList<Contact> getContacts() {
        return contacts;
    }
}

