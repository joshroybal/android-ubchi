package com.example.josh.ubchi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class MainActivity extends Activity {
    public static final String EXTRA_MESSAGE = "Copyright (c) 2017 Josh Roybal";
    TextView text;
    ListView PhoneListView;
    EditText NameTxt, PhoneTxt;
    DoublyLinkedList theList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        theList = new DoublyLinkedList();

        try {
            readFile(theList);
        } catch (IOException e) {
            e.printStackTrace();
        }

        text = (TextView) findViewById(R.id.Listing);
        PhoneListView = (ListView) findViewById( R.id.PhoneListView );
        NameTxt = (EditText) findViewById(R.id.Name);
        PhoneTxt = (EditText) findViewById(R.id.Phone);

        // Add button listener
        Button Add = (Button) findViewById(R.id.Add);
        Add.setOnClickListener(new View.OnClickListener()
        {   @Override
        public void onClick(View v)  {
            // Do whatever you want to do.
            PhoneListView.setAdapter(null);
            String name = NameTxt.getText().toString();
            if (name.length() == 0) return;
            String telephone = PhoneTxt.getText().toString();
            theList.insertNode(name, telephone);
            try {
                writeFile(theList);
            } catch (IOException e) {
                e.printStackTrace();
            }
            text.setText(" Record added.");
            NameTxt.setText("");
            PhoneTxt.setText("");
        }
        });

        // Delete button listener
        Button Delete = (Button) findViewById(R.id.Delete);
        Delete.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v)  {
                // Do whatever you want to do.
                PhoneListView.setAdapter(null);
                String name = NameTxt.getText().toString();
                if (name.length() == 0) return;
                if (theList.deleteNode(name)) {
                    try {
                        writeFile(theList);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    text.setText(" Record deleted.");
                }
                else
                    text.setText(" Target string not found.");
                NameTxt.setText("");
                PhoneTxt.setText("");
            }
        });

        // Modify button listener
        Button Modify = (Button) findViewById(R.id.Modify);
        Modify.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v)  {
                // Do whatever you want to do.
                PhoneListView.setAdapter(null);
                // text.setText("Under development.");
                String name = NameTxt.getText().toString();
                if (name.length() == 0) return;
                Node target = theList.findNode(name);
                if (target != null) {
                    // just update node in this implementation
                    String telephone = PhoneTxt.getText().toString();
                    // delete and then insert, take the easy way out
                    theList.deleteNode(name);
                    theList.insertNode(name, telephone);
                    try {
                        writeFile(theList);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    text.setText(" Record modified.");
                }
                else
                    text.setText(" Target string not found.");
            }
        });

        // Retrieve button listener
        Button Retrieve = (Button) findViewById(R.id.Retrieve);
        Retrieve.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v)  {
                // Do whatever you want to do.
                text.setText("");
                PhoneListView.setAdapter(null);
                String name = NameTxt.getText().toString();
                if (name.length() == 0) return;
                Node target = theList.findNode(name);
                if (target != null)
                    PhoneTxt.setText(target.data);
                else
                    text.setText(" Target string not found.");
            }
        });

        // List button listener
        Button ListAll = (Button) findViewById(R.id.List);
        ListAll.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v)  {
                // Do whatever you want to do
                NameTxt.setText("");
                PhoneTxt.setText("");
                text.setText("");
                populateListVew(PhoneListView, theList);

                // load new activity when user clicks on listview item
                PhoneListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position,
                                            long id) {
                        Intent newActivity = new Intent(view.getContext(), DisplayMessageActivity.class);
                        String item = ((TextView)view).getText().toString();
                        newActivity.putExtra(EXTRA_MESSAGE, item);
                        startActivity(newActivity);
                    }
                });
            }
        });
    }

    // write the linked list to data file
    public void writeFile(DoublyLinkedList someList) throws IOException
    {   Node curr = someList.head;
        if (curr != null) {
            FileOutputStream fos = openFileOutput("list.dat", MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fos);

            BufferedWriter bw = new BufferedWriter(osw);
            do {
                bw.write(curr.key);
                bw.newLine();
                bw.write(curr.data);
                bw.newLine();
                curr = curr.next;
            } while (curr != null);
            bw.close();
        }
        else {
            File dir = getFilesDir();
            File file = new File(dir, "list.dat");
            boolean deleted = file.delete();
        }
    }

    // read the linked list from data file
    public void readFile(DoublyLinkedList someList) throws IOException
    {   FileInputStream fileIn = openFileInput("list.dat");
        InputStreamReader InputRead = new InputStreamReader(fileIn);

        try (BufferedReader br = new BufferedReader(InputRead)) {
            String line;
            while ((line = br.readLine()) != null) {
                // process the line.
                String name = line;
                line = br.readLine(); // okay if data is null
                String telephone = line;
                someList.insertNode(name, telephone);
            }
            br.close();
        }
    }

    // populate the list view
    private void populateListVew(ListView PhoneListView, DoublyLinkedList someList)
    {
        ArrayList<String> contactList = new ArrayList<String>();
        Node curr = someList.head;
        if (curr == null)
        {
            contactList.add(" Empty list.");
        }
        else
        {
            do
            {
                contactList.add(curr.toString());
                curr = curr.next;
            } while (curr != null);
        }

        // Create ArrayAdapter using the contacts list.
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                contactList);

        // Set the ArrayAdapter as the ListView's adapter.
        PhoneListView.setAdapter( adapter );
    }

}

// Objects represents persons and phone nos.
class Node
{   protected String key;       // e.g. first name
    protected String data;      // e.g telephone number
    protected Node previous;
    protected Node next;

    // constructor definition
    Node(String key, String data)
    {   this.key = key;
        this.data = data;
        this.previous = null;
        this.next = null;
    }

    public String toString()
    {   return new String(key + ":\t" + data + "\n");
    }
}

// Objects represents doubly linked lists of nodes
class DoublyLinkedList
{   Node head;           // head of list
    Node tail;           // tail of list

    // constructor definition
    DoublyLinkedList()
    {
        head = null;
        tail = null;
    }

    // public methods

    public void insertNode(String key, String data)
    {   Node node = new Node(key, data);
        Node current = head;

        if (head == null)
        {   // if it's the first member of an empty list
            node.previous = null;
            node.next = null;
            head = node;            // update the leader node
            tail = node;            // update the trailer node
            return;
        }

        // if it's the head node
        if (node.key.compareTo(this.head.key) <= 0)
        {   // first assign the link's pointers
            node.previous = null;
            node.next = head;
            head.previous = node;
            head = node;            // reset the leader node
            return;
        }

        while (current != null && key.compareTo(current.key) > 0)
            current = current.next;

        if (current != null)
        {   // there are nodes in both directions
            // first assign the link pointers
            node.previous = current.previous;
            node.next = current;
            // then the references to the node
            current.previous.next = node;
            current.previous = node;
        }
        else
        {   // at the end
            tail.next = node;
            node.previous = tail;
            node.next = null;
            tail = node;        // rest the trailer noder
        }
    }

    // search for a node by key and return node if match found, null otherwise
    public Node findNode(String tgtstr)
    {   Node current = head;

        /* move through the list until match found */
        while (current != null && tgtstr.compareTo(current.key) != 0)
            current = current.next;
        if (current != null)
            return current;
        else
            return null;
    }

    public boolean deleteNode(String tgtstr)
    {   Node target = findNode(tgtstr);
        if (target == null)
            return false;

        Node previous = target.previous;
        Node next = target.next;

        // we shall take it case by case
        if (previous == null && next == null) { // singleton list
            target = null;
            head = null;
            tail = null;
        }
        else if (previous == null) {        // at head of list
            head = next;
            head.previous = null;
            target = null;
        }
        else if (next == null) {        // at tail of list
            tail = previous;
            tail.next = null;           // previous list member now the end
            target = null;
        }
        else {  // there are list elements in both directions
            target.previous.next = next;        // skip forwards
            target.next.previous = previous;    // skip backwards
            target = null;
        }
        return true;
    }
}
