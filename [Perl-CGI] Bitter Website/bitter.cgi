#!/usr/bin/perl -w

# COMP2041: Assignment 2 - Bitter
# A pseudo 'imitation' of twitter service using CGI
# Nelson Wee, z3352078
# http://cgi.cse.unsw.edu.au/~cs2041/assignments/bitter/

use CGI qw/:all/;
use CGI::Carp qw/fatalsToBrowser warningsToBrowser/;

#login details
$username = param('username');
$password = param('password');
$verified = param('verified');

# Which profile page to open
$userDest = param('userDest');
# Flags to control output
$logOut = param('logout');
$search = param('search');
$profile = param('profile');
$bleat = param('bleat');
$listen = param('listen');
# To delete bleats
$delete = param('delete');
$numDel = param('numDel');
# To reply to bleats
$reply = param('reply');
$replyID = param('replyID');
# Edit
$edit = param('edit');
$editData = param('editData');
$editDone = param('editDone');
# lostPass
$lostP = param('lost');
$lostEmail = param('lostEmail');
$send = param('send');

sub main() {
    # print start of HTML ASAP to assist debugging if there is an error in the script
	print page_header();
    
    # Now tell CGI::Carp to embed any warning in HTML
    warningsToBrowser(1);
    
    # define some global variables
    $debug = 1;
    $dataset_size = "medium"; 
    $users_dir = "dataset-$dataset_size/users";
    $bleats_dir = "dataset-$dataset_size/bleats";
    
    # Debugger statement
	#print "Status: logOut($logOut)<br> search($search)<br> bleat($bleat)<br> del($delete)<br> numDel($numDel)<br> reply($reply)<br> replyID($replyID)<br> edit($edit)<br> editData($editData)<br> editDone($editDone)<br> lostEmail($lostEmail)<br> send($send) <br>";
	
	# apply actions
	if (defined $delete) { 	deleteBleat(); }
	elsif (defined $listen) { changeListeners();}
	elsif (defined $reply) { createBleat();}
	elsif (defined $bleat) { createBleat();}
	elsif (defined $lostP) { ; } 

	if (defined $editData && defined $editDone) { addAbout();}
	if (defined $send) { ; }#sendEmail(); }
	
	#navigation of the site if logged in
	if ($verified == 1) {
		#compile database of bleats
		getBleats();
		
		if (defined $profile) { 
			undef $search;
			print profile_page(); 
		}
		elsif (defined $search && !defined $logOut) {
			print search_page();
		}
		elsif (defined $logOut) {
			undef $verified;
			print login_page()
		}
		else { print profile_page(); }      
	}
	else { print login_page();}
	
    print page_trailer();
}

##########################
# PAGE DATA functions
##########################

# The login page, the default form
sub login_page {
	my $error = 0; #check to see if theres an error in login
	if (defined $username && defined $password) {
		#sanitize username and password
		$username = substr $username,0,256;
		$username =~ s/\W//g;
		$password = substr $password,0,256;
		$password =~ s/\W//g;
		$user_details = "$users_dir/$username/details.txt";		
		if (!open F, "<$user_details") { $error = 1; }
		else {
			#Goes thru details.txt and looks for password
			while($line = <F>) {
				if ($line =~ /password:\s*(\w+)\b/) { #if the category is password
					$userPass = $1; 
					chomp $userPass;
					
					if ($password eq $userPass) {
						#param('verified', 'true');
						$verified = 1;
						print center("Login Successful!");				
						return 
							start_form,
							hidden('verified',$verified),
							hidden('username',$username),
							center submit({-class=>'bitter_button'},'thanks');						
							end_form,			
					}
					else { $error = 2; }
				}
			}			
		}
		close F;
	}
	#Print the login screen if details are undefined or have errors
	if ((!defined $user && !defined $password) or $error) {
		print center(h2(i("Please Login")));
		print start_form;
		$image = "<img width=\"200px\" src=\"./blank.png\">";		
		print center($image),"<br>";
		if ($error == 1) { print center("Invalid username."),"<br>" }
		elsif ($error == 2) { print center("Incorrect Password"),"<br>" }
		
		print center('Username:', textfield('username'));
		print center('Password:', password_field('password'));	
		print "<br>",center(submit({-class=>'bitter_button'},'Login'));	
		print end_form;
		
		#lost password buttono
		my $supportData = "<input type=\"submit\" class=\"bitter_button\" name=\"lost\" value=\"Lost Password\">";
		#if the lost password button was pressed, open the text to receive email
		if (defined $lostP) {
			$supportData = "<input type=\"textfield\" name=\"lostEmail\", value=\"$lostEmail\">";
			$supportData .= "<input type=\"submit\" class=\"bitter_button\" name=\"send\" value=\"Send Password\">";	
		}
print <<lostD	
	<br><br><form method=\"POST\">
		<div style="text-align:center"> $supportData
	</div></form>
lostD
;		
		
	}	
	return hidden($verified);
}

# The main profile page
sub profile_page {
	print control_bar();
	if (!defined $userDest) { $userDest = $username; }

	#read details file and obtain variables
	my $details_filename = "$users_dir/$userDest/details.txt";
    open my $p, "$details_filename" or die "can not open $details_filename: $!";
    $details = join ' ', <$p>;
    close $p;   
    
    # parses data of details file
    my ($fullName, $email, $home_Sub, $home_Long, $home_Lati, $listenList, $userID, $aboutMe) = parseDetails($details);
    # adds the bleats of users followed
	getFollowedBleats($listenList);
	
	#changes the prompt when bleating
    my $promptMsg = "Whats Bleatin?";
    my $listenButton = "";
    
    # Follow/Unfollow button, and bleat prompt
    if ($username ne $userDest) { 
    	$promptMsg = "Bleat at them!"; 
    	my $listeningTo = getListeners();
    	my $val = "";
    	if ($listeningTo !~ /$userDest/) { $val = "Listen"; }
    	else { $val = "Unlisten"; }
    	
    	$listenButton = "<input type=\"submit\" class=\"bitter_button\" name=\"listen\" value=\"$val\">";
    }
    # prints a edit button only if on own profile && not in edit mode
    elsif (!defined $edit) { $listenButton = "<input type=\"submit\" class=\"bitter_button\" name=\"edit\" value=\"Edit\">"; }
	
	if (defined $edit) {
		$listenButton = "<textarea name=\"editData\" style=\"width:310px;height:50px;\" class=\"border\" maxlength=\"142\"></textarea>";
		$listenButton .= "<br><input type=\"submit\" class=\"bitter_button\" name=\"editDone\" value=\"Done\">";
		$listenButton .= "<br><br><i><u> Edit Commands: </u></i>";
		$listenButton .= "<br> Editing Tags      : i.e. \"full_name: New Name\" <br>";
		$listenButton .= "Adding about at the end: i.e. \"I love 2041.\" <br>";
		
		undef $edit;
	}
	
    # BLEAT DISPLAY: Saves all related bleats and html to $bleatData
    my $bleatData = "";
	for $timeS (reverse sort keys %{$allBleats{$userDest}}){
		my $userID = $allBleats{$userDest}{$timeS}[0];
		my $bleatID = $allBleats{$userDest}{$timeS}[6];
		
    	#$userID, $time, $message, $longitude, $latitude, $replyTo, bleetnum
    	$bleatData .= "<div class='border'><table><form method=\"POST\"><tr><td>";
			$bleatData .= "<button type=\"submit\" name=\"profile\" class=\"img_button\">";
			$bleatData .= "<img width=\"150px\" src=\"$users_dir/$userID/profile.jpg\"></button>";
			$bleatData .= "<input type=\"hidden\" name=\"verified\" value=\"$verified\">"; 
			$bleatData .= "<input type=\"hidden\" name=\"username\" value=\"$username\">"; 
	    	$bleatData .= "<input type=\"hidden\" name=\"userDest\" value=\"$userID\"></td>";
			
	    	# Bleat Details
    		$bleatData .= "<td><b> Username: </b> $userID <br>";
			$bleatData .= "<b> Time </b> $allBleats{$userDest}{$timeS}[1]<br>";
			$bleatData .= "<b> Message: </b><font size=\"4\"><i> $allBleats{$userDest}{$timeS}[2] </i></font><br>";
			$bleatData .= "<b> Longitude: </b> $allBleats{$userDest}{$timeS}[3]<br>";
			$bleatData .= "<b> Latitude: </b> $allBleats{$userDest}{$timeS}[4] <br>";
			$bleatData .= "<b> replyTo: </b> $allBleats{$userDest}{$timeS}[5] <br>";
			$bleatData .= "<b> bleatNum: </b> $bleatID";
		
		# Reply box and reply/delete buttons	
		$bleatData .= "</td></tr><tr><td colspan=\"2\">";			
			#clears reply box after pressing reply
			if (defined $reply) { $bleat ="" };
			$bleatData .= "<textarea name=\"bleat\" style=\"width:400px;height:50px;\" class=\"border\" maxlength=\"142\"></textarea><br>";
			$bleatData .= "<input type=\"submit\" class=\"bitter_button\" name=\"reply\" value=\"Reply\">";
			$bleatData .= "<input type=\"hidden\" name=\"replyID\" value=\"$bleatID\">";
			
			#display delete button if bleat owner
			if ($userID eq $username) {		
				$bleatData .= "<input type=\"submit\" class=\"bitter_button\" name=\"delete\" value=\"Delete\">";
				$bleatData .= "<input type=\"hidden\" name=\"numDel\" value=\"$bleatID\">";
			}
    	$bleatData .= "</td></tr></form></table></div>";
    }   	    
        
  	#Table below displays the profile information and bleats in a 2 column 1 row table
	print h2(i("$fullName (\@ $userDest)"));
	return <<profileP	
	<table id="profile" class='profile_table'>
	<tr>
<!--This section displays the user profile information-->

		<td><div class="profile_cell_img">
				<img width="250px" src="$users_dir/$userDest/profile.jpg">
			</div><br>
					
			<div class='profile_info'> <b><u>About Me:</u></b><br><br>
				<b>Name: </b> $fullName <br><br>
				<b>Email: </b> $email <br><br>
				<b>Suburb: </b> $home_Sub <br><br>
				<b>Home Longitude: </b> $home_Long <br><br>
				<b>Home Latitude: </b> $home_Lati <br><br>
				<b>Listening to: </b> $listenList <br><br>
				<b>About Me: </b> $aboutMe <br>
			</div><br>
			<form method="POST" action="">
				$listenButton
				<input type="hidden" name="verified" value="$verified">
			    <input type="hidden" name="userDest" value="$userDest">
			    <input type="hidden" name="username" value="$username">
			</form>
			</td>

			
<!--This section displays the new-bleats and all related bleats-->
						
		<td rowspan="2"><form method="POST">
			<b><font size="4"><i>$promptMsg</font></b></i><br>
			<textarea name="bleat" style="width:400px;height:150px;" class="border" maxlength="142"></textarea><br>
			<input type="submit" class="bitter_button" name="bleat" value="Bleat">
		    <input type="hidden" name="verified" value="$verified">
		    <input type="hidden" name="userDest" value="$userDest">
		    <input type="hidden" name="username" value="$username">
			</form>
		<br><br> $bleatData 
		</td>
	</tr>				
	</table>
profileP
;
}

# The search results page
sub search_page {
	print control_bar();
	@users = sort(glob("$users_dir/*"));
	
	#sanitises search data
	$search =~ s/[^\w \@\,\!\.]//g;		
	print "<br>Searched for \"$search\"<br>";
	$search = lc $search;
	
	# 'boolean' to check if any results are present
	my $noUsers = 1;	
	my $noBleats = 1;
	
	# counter and limit to constrain the number of bleat results
	my $bc=0;
	my $limit = 10;
	
	# headers for search results
	my $userData = "<b><font size=\"5\"> Users: </font></b><br>";
	my $bleatData = "<b><font size=\"5\"> Bleats: </font></b><br>";
	
	print h2("Results:<br>");
	
	# Saves all found users into $userData to display 
	foreach my $userPath (@users) {
		my $user = $userPath;
		$user =~ s/$users_dir\///g;
		$found = 0;
		
		#search for usermame
		if ($user =~ /$search/i) {
			$found = 1;
			$noUsers = 0;
		}
		else { 	#search for fullname 
			open my $p, "$userPath/details.txt" or die "can not open $userPath/details.txt: $!";
			while ($line=<$p>) {
				if ($line =~ /full_name:\s(\w+\s\w+)/i) {
					$name = $1;
					if ($name =~ /$search/i) {
						$found = 1;
						$noUsers = 0;
					}
				}
			}
		}
	
		# If users found, save this profile into userData
		if ($found != 0) {
			open my $p, "$userPath/details.txt" or die "can not open $userPath/details.txt: $!";
		    $details = join ' ', <$p>;
		    close $p;
			my ($fullName, $email, $home_Sub , $home_Long, $home_Lati, $listensList, $userID) = parseDetails($details);
			
			$userData .= "<table id=\"profile\", class=\'border\'><tr><td> $fullName (\@$userID)</td></tr>";
			$userData .= "<tr><form method=\"POST\"><td class=\"profile_cell_img\">";
				$userData .= "<button type=\"submit\" name=\"profile\" class=\"img_button\">";
	  			$userData .= "<img width=\"200px\" src=\"$users_dir/$userID/profile.jpg\">";
				$userData .= "<input type=\"hidden\" name=\"verified\" value=\"$verified\">"; 
				$userData .= "<input type=\"hidden\" name=\"username\" value=\"$username\">"; 
				$userData .= "<input type=\"hidden\" name=\"userDest\" value=\"$userID\">";
				$userData .= "</button></form>";
				$userData .= "<td><div ><br>";
				$userData .= "<b>Name: </b> $fullName <br><br>";
				$userData .= "<b>Email: </b> $email <br><br>";
				$userData .= "<b>Suburb: </b> $home_Sub <br><br>";
				$userData .= "<b>Listening to: </b> $listensList <br>";
			$userData .= "</div></td></tr></table>";
		}	
		
		# Searches through bleats of user and if found, saves  to bleatData (limit = 10)
		for my $timeS (reverse sort keys %{$allBleats{$user}}) {
			my $data = $allBleats{$user}{$timeS}[2];			
			if ($data =~ /$search/i && $bc < $limit ) {
				$bleatData .= "<div class=\'border\'><table id=\"profile\"><tr>";
				$bleatData .= "<td><b> Username: </b> $user <br>";
				$bleatData .= "<b> Time </b> $allBleats{$user}{$timeS}[1]<br>";
				$bleatData .= "<b> Message: </b> $allBleats{$user}{$timeS}[2] <br>";
				$bleatData .= "<b> Longitude: </b> $allBleats{$user}{$timeS}[3]<br>";
				$bleatData .= "<b> Latitude: </b> $allBleats{$user}{$timeS}[4] <br>";
				$bleatData .= "<b> replyTo: </b> $allBleats{$user}{$timeS}[5] <br>";
				$bleatData .= "<b> bleatNum: </b> $allBleats{$user}{$timeS}[6]<br><br>";
				$bleatData .= "</td></tr></table></div>";
			
				$bc += 1;					
				$noBleats = 0;
			}
		}
		
	}
	
	#hides other column if no results were returned. 
	if ($noUsers == 1) { $userData = "";}
	if ($noBleats == 1) { $bleatData = ""; }	
	if ($noUsers == 1 && $noBleats == 1) {
		$userData = "<table><font size=\"5\"> No Results =( </font></table>";
		$bleatData = "";
	}
	
	#only show the first $limit bleats
	if ($bc eq $limit) {
		my $balance = $bc - $limit;
		$bleatData .= "<div ><form method=\"POST\"><table><tr><tb>";
		$bleatData .= "<br><b> $limit Bleats displayed </b>";
		$bleatData .= "<input type=\"submit\" class=\"bitter_button\" name=\"more\" value=\"More\">";
		$bleatData .= "<input type=\"hidden\" name=\"verified\" value=\"$verified\">"; 
		$bleatData .= "<input type=\"hidden\" name=\"username\" value=\"$username\">"; 
    	$bleatData .= "<input type=\"hidden\" name=\"userDest\" value=\"$username\">";
		$bleatData .= "</tb></tr></table></form></div>";
	}
	
	return <<test	
<table id="profile" class='search_table'>
	<tr>
		<td> $userData </td>
		<td> $bleatData </td>
	</tr>
</table>
test
;
}

# The control bar [Profile, Search, Logout]
sub control_bar {
	return <<cBar
	<br>
	<P ALIGN="right"><i><font size="4"> Search for people </font></i></P>
	<table id="control" class="controlBar" style="width:100%;height:100%;">
  		<form method="POST">
  		<tr>
  			<td><button type="submit" name="profile" class="img_button"> 
  					<img width="60px" src="$users_dir/$username/profile.jpg">
  				</button>
  			<td style="width:95%"><b> \@ $username </b></td>
    		<td><input type="textfield" name="search", value="$search"></td>
    		<td><input type="submit" class="bitter_button", name="search", value="Search">
    			<input type="hidden" name="verified" value="$verified"></td>
    		<td><input type="submit" class="bitter_button", name="logout", value="Logout"></td>
 				<input type="hidden" name="username" value="$username">
 				<input type="hidden" name="userDest" value="$username">
  		</tr></form>
</table>
cBar
}

##########################
# READING DATA functions
##########################

# Parses details file and returns an array of variables
sub parseDetails {
	(my $detaiils) = @_;
	$categories= "(full_name:|email:|listens:|username:|password:|home_latitude:|home_longitude:)";
    
    my $fullName = "";
	if ($details =~ /full_name:\s(\w+\s+\w+)/) { $fullName = "$1";}
    
    #gets email data, blank if doesnt exist
    my $email = "";
	if ($details =~ /email:\s([\w\.]+@[\w\.]+)/) { $email = $1; }
    
    my $home_Sub = "";
	if ($details =~ /home_suburb:\s(.*)/) { $home_Sub = $1; }
    
    my $home_Long = "";
	if ($details =~ /home_longitude:\s([0-9\-.]+)/) { $home_Long = $1; }

	my $home_Lati = "";
	if ($details =~ /home_latitude:\s([0-9\-.]+)/) { $home_Lati = $1; }
    	    
    my $listens = "";
	if ($details =~ /listens:\s(.*)/) { $listens = $1; }
   
	my $userID = "";
	if ($details =~ /username:\s(\w+)/) { $userID = $1;}
	
	my $aboutMe = "";
	if ($details =~ /about_me:\s(.*)/) { $about_me = $1;}
    
    return ($fullName, $email, $home_Sub, $home_Long, $home_Lati, $listens, $userID, $about_me);
	
}

# Parses bleats file and returns an array of variables
sub parseBleats {
	(my $bleat) = @_;
	$categories= "(time:|bleat:|longitude:|latitude:|username:|in_reply_to:)";
	
    $bleat =~ /time:\s([0-9]+)/;
    my $time = "$1";
    
    #gets bleat data, blank if doesnt exist
    my $message = "";
    if ($bleat =~ /bleat:\s(.*)/) { $message = $1; }
	
	my $longitude = "";
    if ($bleat =~ /longitude:\s([0-9\-.]+)/) { $longitude = $1 };
	
	my $latitude = "";
    if ($bleat =~ /latitude:\s([0-9\-.]+)/) { $latitude = $1 };
	
	my $userID = "";   	    
    if ($bleat =~ /username:\s(\w+)/) { $userID = $1; }
    
	my $replyTo = "";
	if ($bleat =~ /in_reply_to:\s([0-9]+)/) { $replyTo = $1;}
        
    return ($userID, $time, $message, $longitude, $latitude,$replyTo);
	
}

# Saves the direct (made by user) and indirect (mentioned user) bleats, into allBleats hash 
sub getBleats {
	# for all the bleats
	foreach $bleatNum ((glob "$bleats_dir/*")) {		
		open my $p, "$bleatNum";		
    	my $bleat = join '', <$p>;
    	close $p;
    
		@bleatData = my ($userID, $time, $message, $longitude, $latitude, $replyTo) = parseBleats($bleat);
		$num = $bleatNum;
		$num =~ s/$bleats_dir\///g;
		push @bleatData, $num;
		
		#save bleat of sender
		$allBleats{$userID}{$time} = [@bleatData];
		
		#save bleats that mention this user with @user
		if ($message =~ /\@(\w+)/) {
			$allBleats{$1}{$time} = [@bleatData];
		}
	}
}

# Store bleats of those followed into allBleats hash, only called on profile
sub getFollowedBleats {
	my ($listens) = @_;
	#foreach follower
	@people = $listens =~ /(\w+)/g;
	foreach my $user (@people) {
		for $times (reverse sort keys %{$allBleats{$user}}){		
			# if the follower made the bleat then save the bleat to the current person's feed
			if ($allBleats{$user}{$times}[0] eq $user) {
				$allBleats{$userDest}{$times} = $allBleats{$user}{$times};			
			};
		}
	}
}

##########################
# EDITING DATA functions
##########################

# Writes bleat to file
sub createBleat {
	#if bleating from someone elses page, tag them in
	my $bleatMsg = "";
	if ($username ne $userDest) { $bleatMsg .= "\@$userDest "; }   
	elsif (defined $reply) {
		my ($userID, $time2, $message2, $longitude2, $latitude2,$replyTo2) = parseBleats($replyID);
		$bleatMsg .= "\@$userID ";
	}
	$bleatMsg .= $bleat;	
	undef $bleat;
		
	#find the latest bleat number and add to it.
	my @bleats = sort(glob("$bleats_dir/*"));
	my $bleatNum = $bleats[$#bleats];
	$bleatNum =~ s/$bleats_dir\///g;
	$bleatNum += 1;
	#Adds directory to number
	$bleatNum = "$bleats_dir/"."$bleatNum";
	my $time = time;
	
	#write to FILE unless theres an error
	unless (open FILE, '>'.$bleatNum) {
		die "Unable to bleat";
	}
	
	#write bleat details
	print FILE "username: $username\n";
	print FILE "bleat: $bleatMsg\n";
	print FILE "time: $time\n";
	print FILE "longitude: 0.00\n";
	print FILE "latitude: 0.00\n";
	print FILE "in_reply_to: $replyID\n";
	
	close FILE;
}

# Function to add/remove listeners in details.txt
sub changeListeners {
	my $cmd = $listen;
	open my $p, "$users_dir/$username/details.txt";
	@allLines = <$p>;
	close $p;
	
	my $i=0;	
	foreach $line (@allLines) {
		#add user to  list if Listen selected
		if (($cmd eq "Listen") && ($line =~ /listens:\s(.*)/)) {
			chomp($line);
			$line .= " $userDest\n";
			$allLines[$i] = $line;
		}	
		#remove user from list if Unlisten selected
		elsif (($cmd eq "Unlisten") && ($line =~ /listens:\s(.*)/)) {
			$line =~ s/$userDest//g;
			$allLines[$i] = $line;
		}
		$i++;
	}
		
	unless (open FILE, '>'."$users_dir/$username/details.txt") {
		die "Unable to change Listen status";
	}
	
	foreach $line (@allLines) {
		print FILE "$line";
	}
	close FILE;
}
# simple helper function to obtain the list of listeners for the current user
sub getListeners {
	open my $p, "$users_dir/$username/details.txt";
	while ($line = <$p>) {
		if ($line =~ /listens:\s(.*)/) {
			return $1;
		}
	}
	close $p;
}


# Function edits the details of the user 
sub addAbout {
	open my $p, "$users_dir/$username/details.txt";
	@allLines = <$p>;
	close $p;
	
	$categories= "(full_name:|email:|listens:|username:|password:|home_latitude:|home_longitude: |about_me:)"; 
	
	$i=0;
	foreach $line (@allLines) {
		# search for categories
		if (($line =~ /$categories(.*)/)) {
			my $selectedCategory = $1;
			my $existingData = $2;
			
			#only remove the data and replace if the category exists in edited data
			if ($editData =~ /$selectedCategory(.*)/) {
				my $replacement = $1;
				#remove existing data
				$line =~ s/$existingData/$replacement/g;
				$allLines[$i] = $line;
			}
		}	
		$i++;
	}
	
	#add the about me section
	if ($editData =~ /[^$categories](.*)$/) {
		push @allLines, "about_me: $1";
	}
		
	unless (open FILE, '>'."$users_dir/$username/details.txt") {
		die "Unable to change add about data";
	}
	
	foreach $line (@allLines) {
		print FILE "$line";
	}
	close FILE;
}

# deletes the designated bleat
sub deleteBleat {
	my $file_dir = "$bleats_dir/$numDel";
	#print "Attempting to delete $file_dir<br>";
	my $num = unlink $file_dir;
	#print "$num files removed<br>";	
}


##########################
# OTHER functions
##########################

# Attempt to send email to user, but involves external modules
sub sendEmail {
	my $message = Email::MIME->create(
  	header_str => [
    	From    => 'Team Bitter',
    	To      => $lostEmail,
    	Subject => 'Password Recovery',
  	],
  	attributes => {
    	encoding => 'quoted-printable',
    	charset  => 'ISO-8859-1',
  	},
  	body_str => "Recover password was selected: Here is your password. \n",
	);

	# send the message
	#use Email::Sender::Simple qw(sendmail);
	sendmail($message);
}

# Initial Template
# Show unformatted details for user "n".
# Increment parameter n and store it as a hidden variable
#
sub user_page {
	#print control_bar();
    my $n = param('n') || 0;
    my @users = sort(glob("$users_dir/*"));
    my $user_to_show  = $users[$n % @users];
    my $details_filename = "$user_to_show/details.txt";
    open my $p, "$details_filename" or die "can not open $details_filename: $!";
    $details = join '', <$p>;
    close $p;
    my $next_user = $n + 1;
    #print "v: $verified", "<br>";
    return <<eof
<div class="bitter_user_details">
$details
</div>
<p>
<form method="POST" action="">
    <input type="hidden" name="verified" value="$verified">
	<input type="hidden" name="n" value="$next_user">
    <input type="submit" value="Next user" class="bitter_button">
</form>
eof
}

##########################
# HTML functions
##########################

#
# HTML placed at the top of every page
#
sub page_header {
    return <<eof
Content-Type: text/html

<!DOCTYPE html>
<html lang="en">
<head>
<title>Bitter</title>
<link href="bitter.css" rel="stylesheet">
</head>
<body>
<div class="bitter_heading">
Bitter
</div>
eof
}

#
# HTML placed at the bottom of every page
# It includes all supplied parameter values as a HTML comment
# if global variable $debug is set
#
sub page_trailer {
    my $html = "";
    $html .= join("", map("<!-- $_=".param($_)." -->\n", param())) if $debug;
    $html .= end_html;
    return $html;
}

main();
