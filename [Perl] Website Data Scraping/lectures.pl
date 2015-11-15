#!/usr/bin/perl

#This script scrapes UNSW lecture times from their website for all given courses.
# No option would print the value the course lectures in the same format as the website
# -d option prints hourly details of lectures  of each course
# -t option prints a timetable of all courses and their lectures

$option = $ARGV[0];
#Checks if there is an option, and removes first argument if it is
if ($option =~ /\-\w/) { shift @ARGV } 
else { $option = 0 }

#for each course given
foreach $course (@ARGV) {
	$course =~ /^[a-zA-Z]{4}[0-9]{4}\b/ or die "Code must be 4 letters and 4 numbers\n";
	$url = "http://www.timetable.unsw.edu.au/2015/$course.html";
	$section = "";
	%times = ();	#clears hash for subsequent courses
	
	open F, "wget -q -O- $url|" or die;
	while ($line = <F>) {		
		if ($line =~ />(Lecture)<\/a>/) {		
			$period = <F>;
			$period =~ />(\w)(\w+)</;
			$num = $2;
			
			#checks the period type
			if ($1=~ /(U)/) { $period = "X" }
			else { $period = "S"}
			$period .= $num; #appends number to char
			
			#skips class, selection, status, capacity and ends at date
			for ($i=0; $i<5;$i++) {
				$date = <F>; 
				if ($i == 1) { $section = $date} 	
			}
		
			#extracts date+time
			$date =~ />([\w\s\-,:\(\)]+|)</;
			$date = $1; 
			
			#ensures dates are unique + another option not selected
			if ( ! exists $times{$date} && $section !~ /WEB/) {
				$times{$date} = 1;
				#output based on option selected	
				if ($option =~ /0/) {
					print "$course: $period $date\n";
				}
				#option either -d or -t
				elsif ($option =~ /\-d|\-t/) {
					while ($date=~ /((\w\w\w) ([0-9: \-]+))/gi) {
						$day = $2;
						#gets the start/end times for each session
						$session = $3;
						$session =~ /(\w\w):\w\w - (\w\w):(\w\w)/;
						$start = $1;
						$end = $2;
						
						#checks if there is time after the hour when it ends
						$ex = 0;
						if ($3 =~ /[^0]+/) {$ex = 0.5}
						
						#loops the hours in that session
						for ( ; $start < $end+$ex ; $start++) {
							#removes the first '0' in 09 for printing or as a key for the hash
							if ($start =~ /^0/) { $start =~ s/^0//g;}
							
							#prints output if option -d
							if ($option =~ /\-d/) {	print "$period $course $day $start\n" }
							
							#stores values in timetable hash if option -t
							elsif ($option =~ /\-t/) {
								if ( exists	$timetable{$period}{$start}{$day} ) {
									#print "EXISTS day is >$day< time is >$start<\n";
									$timetable{$period}{$start}{$day} += 1;
								}
								else { $timetable{$period}{$start}{$day} = 1}						
							}
						}
					}				
				}
			}
		}
	}
}

#prints the timetable if option -t
if ($option =~ /\-t/) {
	#for each period (S1,S2, X1, etc)
	foreach $period (keys %timetable) {	
		printf("%-5s %6s %6s %6s %6s %6s\n",$period, "Mon", "Tue", "Wed", "Thu", "Fri");
		for ($start=9; $start < 21; $start++) {
			#adds the 0 for the time column
			$outtime = "";
			if ($start == 9) { $outtime = 0 }
			$outtime .= "$start:00";

			#initialises hash if not initialised. For warning.
			if ( ! exists $timetable{$period}{$start}{"Mon"} ) { $timetable{$period}{$start}{"Mon"} = "" }
			if ( ! exists $timetable{$period}{$start}{"Tue"} ) { $timetable{$period}{$start}{"Tue"} = "" }
			if ( ! exists $timetable{$period}{$start}{"Wed"} ) { $timetable{$period}{$start}{"Wed"} = "" }
			if ( ! exists $timetable{$period}{$start}{"Thu"} ) { $timetable{$period}{$start}{"Thu"} = "" }
			if ( ! exists $timetable{$period}{$start}{"Fri"} ) { $timetable{$period}{$start}{"Fri"} = "" }
			
			#prints the number of courses in each day for every hour
			printf("%5s %5s %6s %6s %6s %6s\n",$outtime, 
			$timetable{$period}{$start}{'Mon'}, $timetable{$period}{$start}{'Tue'}, 
			$timetable{$period}{$start}{'Wed'}, $timetable{$period}{$start}{'Thu'}, 
			$timetable{$period}{$start}{'Fri'});
		}
	}
}
