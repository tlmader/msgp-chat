# msgp-chat

**Computer Networks and Telecommunications Programming Assignments 2 and 3**

A simple chat application that consists of client and server components, which communicate via the *MsgP* protocol specified below.

The basic functionality of the server is as follows:
* It maintains a non-persistent list of chat groups (groups are created on demand and are not persistent across server executions.
* It allows clients to join any of the existing groups, or create a new one implicitly by requesting to join a group that does not exist.
* Upon request, it provides to clients a list of the available groups, the current membership of the group, and the history of message sent to a particular group.
* It allows clients to send messages to individual users, a specific group, or multiple groups.
  
## Getting Started

Client: `java csci4311.chat.CLIUserAgent <user> <server> [<port>]`

Server: `java csci4311.chat.ChatServer [<port>]`

The default port (if not specified) is 4311.

## User Agent

The agent shows a prompt of the form `@[user]>>`, e.g., `@alice >>`.

Messages received are displayed in the form `[<sender>] <message>`.

The user agent reads all user input from the standard input stream and supports the following single-line commands:

### `join <group>`

Joins the specified group and reports current number of members.

Example:

    join 4311
    Joined #4311 with 3 current members.

### `leave <group>`
Leaves the specified group, no output on success. Returns error if trying leave a group of which the user is not a member.

Example:

    leave 4311
    [bob] sorry I am late
    [3/5]
    leave 4311
    Not a member of #4311.
  
### `groups`
List the currently available groups and the number of members. No output if no groups.

Example:

    groups
    #4311 has 12 members
    #5311 has 3 members
    #test has 0 members

### `users <group>`
Lists group membership. Error if no such group, no output if no members.

Example:

    users 4311
    @alice @bob

### `history <group>`
Lists the log of messages for a particular group.

Example:

    history 4311
    [alice] hello
    [bob] sorry I am late
    [charlie] let’s get started

### `send <recipient list> <message>`
Sends a message to users & groups on the list.
* Error if any of the recipients do not exist.
* Individual users are prefaced by `@` and groups by `#`.
* Messages sent to a group(s) become part of the history of all those group(s).

Example:

    send @bob #4311 #5311 when is the assignment due?

## MsgP Spec
All commands are in plaintext ASCII format, and they all start with the string `msgp`. Users and groups are alphanumeric strings of up to 32 characters.

### Join
Adds a user to a chat group.
* If the group does not yet exist, it is created (reply code `200`).
* If the user is already a member, no action is required (reply `201`).

Format:

    msgp join <user> <group>
      
Example:

    msgp join alice 4311
      
### Leave
Removes a user from a chat group (`200`).
* If user is not a member (but the group exists) return `201`.
* If the group does not exist, return `400`.

Format:

    msgp leave <user> <group>
      
Example:

    msgp leave alice 4311
  
### Groups
Enumerated the current list of groups, including empty ones (`200`).
* Group names are returned one per line, with a blank line marking the end of the list.
* `201` if empty result.

Format:

     msgp groups

Example response:

    msgp 200 ok
    csci
    4311
    5311
    <cr><lf>

### Users
Enumerates the current list of users for a given groups (`200`).
* User names are returned one per line, with a blank line marking the end of the list.
* `201` if empty result, `400` if no such group exists.

Format:

    msgp users <group>
  
Example:

    msgp users 4311

Example response:

    msgp 200 ok
    alice
    bob
    <cr><lf>

### Send
Sends a message to the given list of recipients.
* Group messages becomes part of the history for that group.
* The sender is given by a from: header clause, the recipients by one, or more, to: headers.
* User names are prefaced with the `@` character, whereas groups by the `#` character.
* Returns `200` upon success, `400` if any of the recipients does not exist.

Format:

    msgp send
    from: <user>
    to: @<user>|#<group>
    to: ...
    <cr><lf>
    <single line message>
    <cr><lf>

Example:

    msgp send
    from: alice
    to: @bob
    to: #4311
    <cr><lf>
    hi bob!
    <cr><lf>

### History
Provides a chronological list of messages for a given group (`200`).
* `201` if empty result, `400` if no such group.

Format:

    msgp history <group>

The format of the messages is exactly the same as in the case of send.

For example, the request:

    msgp history 4311

Would yield something like:

    msgp 200 OK
    msgp send
    from: alice
    to: #4311
    <cr><lf>
    hello everyone!
    <cr><lf>
    msgp send
    from: bob
    to: #4311
    <cr><lf>
    sorry for being late!
    <cr><lf>
      
### Reply codes
Server always precedes its answer (if any) with one of three reply code, single-line messages:

    msgp 200 OK
    msgp 201 No result
    msgp 400 Error
