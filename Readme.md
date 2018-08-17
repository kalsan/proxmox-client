# proxmox-client

A very simple and hacky java client for launching a SPICE remote-viewer process having secure access to a virtual machine running on Proxmox. Long story short: Use this and you will be able to double-click on your desktop after which a window with a SPICE connection to a VM running on your Proxmox server will magically appear.

## Why?

Proxmox uses tickets that expire, so when you download a .vv file on the web interface and happily connect to a VM using remote-viewer opening that .vv file, you will be terrified tomorrow when the .vv is not working any more. So I built proxmox-client that gets a new ticket for you and fires up remote-viewer for you.

## How?

The client authenticates with Proxmox through its API and gets valid SPICE credentials including the said ticket. It then launches a remote-viewer process to access a given machine through these credentials. Fully supports Proxmox' proxy, no server side hacks required.

Tested on Linux and Windows. Requires Java 8 or higher.

## Usage

proxmox-client.jar -h host -p port -u username -s password -r realm (e.g. pam) -c path/to/remote-viewer.exe -n node-name -v vm-id

- host: Fully qualified domain name of host (also successfully tested with stuff like pve.lan and /etc/hosts set up accordingly)
- port: Typically 8006
- username: Your user you use to connect to Proxmox via its web interface
- password: That user's password. "s" for secret ;-)
- realm: Usually pam or pve, see https://pve.proxmox.com/wiki/User_Management#pveum_authentication_realms
- path to remote-viewer: On Linux, simply use remote-viewer. On Windows, an example would be "C:\Program Files\VirtViewer v6.0-256\bin\remote-viewer.exe"
- node name: The name of the node the VM is currently running on. If you migrate VMs a lot, proxmox-client might be too simple for you (fork it as you wish)
- vm id: The ID of the VM you want to connect to, e.g. 100

## User and password note
 
If you're a sane person, you don't wanna expose your root password by putting it into some desktop shortcut. Create a pam or pve user and give it a random password and the minimum privileges needed to use the VM. Then use those credentials for proxmox-client.

## Shortcuts!

I promised you a double-click solution. Take this .jar and place it somewhere convenient. Then (on Windows) drag-and-drop it on your desktop while holding the Alt-key. 
It will create a shortcut. Right-click that and under "command" fill in the arguments described above right behind the path to the jar. Merry christmas!


## Not good enough?

I spent 2 hours building this script using stack-overflow-driven programming. In other words: it's a hack. If you like to fork it and submit a pull request doing things better, your kindness will bring light and warmth to my heart.

## Thanks

The following resources / discussions were of great help:

- https://github.com/EnterpriseVE/eve2pve-api-java
- https://forum.proxmox.com/threads/remote-spice-access-without-using-web-manager.16561/page-2
- https://stackoverflow.com/questions/7341683/parsing-arguments-to-a-java-command-line-program
