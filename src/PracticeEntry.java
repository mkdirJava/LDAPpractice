import java.util.Arrays;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public class PracticeEntry {

	
	private static final String OBJECT_TYPE="InetOrgPerson";
	
	public static void main(String[] args) {
		
	

		try {
			Hashtable<String, String> ldapEnv = new Hashtable<>();
			ldapEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
			ldapEnv.put(Context.PROVIDER_URL, "ldap://localhost:10389");
			ldapEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
			ldapEnv.put(Context.SECURITY_PRINCIPAL, "uid=admin,ou= system");
			ldapEnv.put(Context.SECURITY_CREDENTIALS, "secret");
			DirContext dircontext = new InitialDirContext(ldapEnv);
			

			createUser(dircontext, 1);
			createUser(dircontext, 2);
			createUser(dircontext, 3);
			createUser(dircontext, 4);

			doSearch(dircontext);
			
			
			doSearchSchema(dircontext);
			
			

			System.out.println(" success");
		} catch (Exception e) {

			System.out.println(e.getMessage());
			
			Arrays.asList(e.getStackTrace()).stream().forEach(stackTrace->{
				System.out.println(stackTrace);
			});
			System.out.println("Somthing went wrong");
		}

	}

	private static void doSearchSchema(DirContext dircontext) throws NamingException {
		String searchFilter="(objectClass=metaSchema)";
		String[] requiredAttributes={"cn"};
		SearchControls controls=new SearchControls();
		controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		controls.setReturningAttributes(requiredAttributes);
		NamingEnumeration<SearchResult> result =  dircontext.search("ou=schema", searchFilter, controls);
		
		while(result.hasMore())
		{
			SearchResult searchResult=(SearchResult) result.next();
			Attributes attr=searchResult.getAttributes();
			
			String commonName=attr.get("cn").get(0).toString();
			
			System.out.println("Name = "+commonName);
			System.out.println("-------------------------------------------");
			
		}
	}

	private static void doSearch(DirContext dircontext) throws NamingException {

		String searchFilter="(objectClass="+OBJECT_TYPE +")";
		String[] requiredAttributes={"sn","cn"};
		SearchControls controls=new SearchControls();
		controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		controls.setReturningAttributes(requiredAttributes);
		NamingEnumeration<SearchResult> result = dircontext.search("ou=system", searchFilter, controls);
	
		while(result.hasMore())
		{
			SearchResult searchResult=(SearchResult) result.next();
			Attributes attr=searchResult.getAttributes();
			
			String commonName=attr.get("cn").get(0).toString();
			String surName=attr.get("sn").get(0).toString();
			System.out.println("Name = "+commonName);
			System.out.println("Surname  = "+surName);
			System.out.println("-------------------------------------------");
			
		}

	}

	private static void createUser(DirContext dircontext, Integer userId) throws NamingException {

		Attributes attributes = new BasicAttributes();
		Attribute attribute = new BasicAttribute("objectClass");
		attribute.add(OBJECT_TYPE);
		attributes.put(attribute);
		Attribute sn = new BasicAttribute("sn");
		sn.add("LastName " + userId);
		Attribute cn = new BasicAttribute("cn");
		cn.add("FirstName " + userId);

		attributes.put(sn);
		attributes.put(cn);
		dircontext.createSubcontext("employeeNumber=" + userId + " ,ou=users,ou=system", attributes);
		System.out.println("Created User " + userId);

	}

}
