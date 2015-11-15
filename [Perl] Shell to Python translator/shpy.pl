#!/usr/bin/perl

# Shell Script to Python translator written in Perl
# written by Nelson Wee, nwee991
# s2 2015 COMP2041/9041 assignment 
# http://cgi.cse.unsw.edu.au/~cs2041/assignment/shpy

#import flags
$globFlag=0;
$osFlag=0;
$subFlag=0;
$sysFlag=0;

#control flags
$loopFlag=0;
$ifFlag=0;

#helper regex used for line comments and shell keywords
$commentCheck = qr/(\s*#.*)?/;
$shellKeywords = qr/(pwd|id|date|ls|cmd|cd|read|exit)/;

@input=<>;
while (my $line = shift @input) {
	chomp $line;
	#converts #!/bin/bash line
	if ($line =~ /^#!/) {
		print "#!/usr/bin/python2.7\n";
		
		#use function to go thru the code to determine what to import
		getFlags(@input);
		if ($globFlag == 1) { print "import glob\n" }
		if ($osFlag == 1) { print "import os\n" }
		if ($subFlag == 1) { print "import subprocess\n" }
		if ($sysFlag == 1) { print "import sys\n" }	
	} 
	###############################################
	#BLANK/COMMENT, matches lines to leave unchanged
	###############################################
	elsif ($line =~ /^\s*#/ || $line =~ /^\s*$/) {
		if ($line =~ /^\s*$/) { print "\n";}
		else { print "$line\n" }
	}
	
	###############################################
	#ECHO LINES, matches echo 
	###############################################
	elsif ($line =~ /echo\s*([^#]*)$commentCheck/) {	
		$text = $1;
		
		#initialises comment if empty
		$comment = commentDefine($2);				
		printTab(0);
		
		print "print ";
		$startFlag=1;
		#goes thru line for: "text ", 'text ',$var, words
		while ($text =~ /(("[^']+")|('+[^']+'+)|(\$?[^ ]+))/gi) {  			
			#the word/words/var to output
			$textOut = $1;
			#if not the start of line			
			if ($startFlag != 1) { print", "}
			#preserves and prints quoted text
			if ($textOut =~ /('|")/) { 
				$type=$1;
				$textOut =~ s/$type//g;
				$prevVal = -1;
				while($textOut =~ /([^\s]+)/gi) {
					#If theres a variable mid quote
					if ($1 =~ /\$(\w+)/) {
						if ($prevVal == 0) { print "$type, " }
						elsif ($prevVal == 1) { print ", " }
						if ($1 =~ /^\s*([0-9]+)/) {	print "sys.argv[$1]"}
						else {	print "$1" }
						$prevVal = 1;
					}
					#If quote continues after a variable
					elsif ($prevVal != 0) { 
						if ($prevVal == 1) { print ", "}
						print "$type$1";
						$prevVal = 0;
					}
					#Print words of quote
					else { print " $1" };
				}
				#end quote if last word was not a variable
				if ($prevVal == 0) { print "$type" }
			}
			#prints single words with quotations					
			elsif ($textOut !~ /(\$\w+)/) { print "'$textOut'"}
			#prints argument vals with sys.argv					
			elsif ($textOut =~ /\$([0-9]+)/) { 
				$arg = $1;
				$arg =~ s/\$//g;
				print "sys.argv[$arg]";
			}			
			#prints variables without the $
			else { 	
				$var = $1;
				$var =~ s/\$//g;
				print "$var";
			}
			$startFlag=0;
		}
		print" $comment\n";	
	}
	
	###############################################
	#SHELL KEYWORDS, matches (pwd|id|date|ls) also arguments $@
	###############################################
	elsif ($line =~ /(^\s*(\$[0-9@])?\s*$shellKeywords\s*[^#]*$commentCheck)/) {
		$cmd = $3;
		#initialises comment if empty
		$comment = commentDefine($4);
		printTab(0);
		#removes comment if exist
		$line =~ s/($comment)//g;		
		
		#if cmd is (read|exit|cd)
		if ($cmd =~ /(^\s*(read|exit|cd)\s*)/) { 
			$line =~ s/^\s*($cmd)\s*//g;
			if ($line =~ /(^\s*([\w\.\/]+)\s*)/) {
				$arg = $1;
				if ($cmd =~ /^\s*cd/) {
					print "os.chdir('$arg')";
				}				
				elsif ($cmd =~ /^\s*read/) {
					print "$arg = sys.stdin.readline().rstrip()";
				}
				elsif ($cmd =~ /^\s*exit/) {
					print "sys.exit($arg)";
				}
			}
		}				
		#if cmd is (pwd|id|date|ls)
		elsif ($line =~ /(^\s*$shellKeywords\s*$)/) { 
			print"subprocess.call(['$1'])";
		}
		#if cmd is (pwd|id|date|ls) with options 
		else {
			$prevVal = -1;
			print "subprocess.call(";
			while($line =~ /([^\s]+)/gi) {
				#prints arguments for subprocess calls
				if ($1 =~ /\$([0-9_!@])/) {
					$var = $1;
					if ($1 =~ /@/) { $var = "1:"; } #from index 1+
					if ($prevVal == 0) { print "] + " } 						
					if ($prevVal == 1) { print " + " } 						
					
					print "sys.argv[$var]";
					$prevVal=1; #argument
				}
				#prints commands for subprocess calls
				elsif ($prevVal != 0) { 
					if ($prevVal == 1) { print " + "}
					print "['$1'";
					$prevVal=0; #cmd
				}
				else { print ", '$1'"}				
			}
			if ($prevVal == 0) { print "]"}
			print ")";
		}
		print" $comment\n";	#print endline for line
	}
	
	###############################################
	#VARIABLE EQ,  matches variable equations
	###############################################
	elsif ($line =~ /(\w+)=('|")?(\$?[_@!\w ]+|`[^#]*)('|")?$commentCheck/) {
		$LHS = $1;
		$RHS = $3;
		$comment = commentDefine($5);
		printTab(0);
		
		print "$LHS = ";
		#if var= text
		if ($RHS =~ /^(('|")?([a-zA-Z][\w+ ]*)('|")?)/) {
			print "'$3'";
		}
		#if var= argument/variable
		elsif ($RHS =~ /^\$(\w+)/) {
			if ($1 =~ /^([0-9]+)/) { print "sys.argv[$1]"}
			else { print "$1" }
		}
		# if var= number
		elsif ($RHS =~ /^([0-9]+)/) {
			print "$1";
		}
		# if var=`expr $number + 1`
		elsif ($RHS =~ /^`expr\s+(.*)/) {
			$RHS =~ s/(`|expr |\s*$)//g;
			#goes thru each value of the expression	
			while($RHS =~ /([^\s]+)/gi) {
				$val = $1;
				#if matches variable
				if ($val=~ /\$([a-zA-Z]+([_\w+]*))/) {
					print "int($1)";
				}
				#if matches opcode
				elsif ($val=~ /(\+|-|\/|\*)/) {
					print " $val ";
				}
				#if matches number
				elsif ($val=~ /[0-9]+/) {
					print "$val";
				}
			}
		}
		print " $comment\n";
	}

	###############################################
	#LOOPS FOR WHILE: Matches loop lines
	###############################################
	elsif ($line =~ /(^\s*(for|while)\s*([^#]*)$commentCheck)/) {	
		$control = $2;
		$remainder = $3;
		
		$comment = commentDefine($4);
		printTab(0);
				
		#FOR loops
		if ($control =~ /^for/) {
			if ($remainder =~ /([\w_]+) in ((\$?\w+\s*)+|(\Q*\E(\.\w+)?)|("|')?\$@("|')?)/) {	
				$var = $1;
				$loopArg = $2;
				@words = ();
				@words = $loopArg =~ /\$?\w+/g;
		
				print "for $var in ";
				# for loops with 'in *.filetype'
				if ($loopArg =~ /\s*\Q*\E\s*(\.\w+)?/) { 
					print "sorted(glob.glob(\"$loopArg\"))" 
				}
				elsif ($loopArg =~ /("|')?(\$@)("|')?/) { 
					print "sys.argv[1:]"; 
				}
				# for loops with 'word number word'
				else {
					$startFlag=1;
					foreach $word (@words) {
						if ($startFlag != 1) { print ", " }
						if ($word =~ /[0-9]+/) { print "$word" }
						#handles variables
						elsif ($word =~ /\$\w+/) {
							$word =~ s/\$//g;
							print "$word"; 
						}
						else { print "'$word'"}		
						$startFlag=0;
					}
				}
			}
		}
		#WHILE loops
		elsif ($control =~ /^while/) {
			$remainder =~ s/(\[|\[\[|test|\]|\]\])\s*//g;
			print "while ";
			
			#goes thru each component of while loop to print out valid
			while($remainder =~ /([^\s]+)/gi) {
				$val = $1;
				#print "\nval IS  >$1<\n";
				if ($val=~ /\$([a-zA-Z]+([_\w+]*))/) {
					print "int($1)";
				}
				elsif ($val =~ /(-\w+|==|>=|<=|<|>|!=|=)/) {
					$op = getOp($1);
					print " $op ";
				}
				elsif ($val =~ /\s*([a-zA-Z]+)\s*/) {
					print "'$1'";
				}
				elsif ($val =~ /\s*([0-9]+)\s*/) {
					print "$1";
				}
			}
		}
		print ": $comment\n";
		
		#skips the next do command and sets 'loopFlag'
		$line = shift @input;
		chomp $line;
		$loopFlag += 1;
	}

	###############################################
	#IF ELIF, matches if elif statements
	###############################################
	elsif ($line =~ /(^\s*(if|elif)\s+([^#]*)$commentCheck)/) {	
		$control = $2;
		$type = $3;
		$type =~ s/(\[|\[\[|test|\]|\]\])\s*//g;
		$comment = commentDefine($4);

		#elif, indent one less than normal same as prev if
		if($control =~ /elif/) {printTab(-1)}
		else { printTab(0)}
		
		#if with options if -r /dev/null
		if ($type =~ /((-\w)\s+(.*)\s*)/) {
			$option=$2;
			$path=$3;
			$path =~ s/\s*$//g;
			print "$control ";
			if ($option =~ /-r/) {
				print "os.access('$path',os.R_OK)";
			}
			elsif ($option =~ /-d/) {
				print "os.path.isdir('$path')";
			}
		}
		elsif ($type =~ /((\$?\w+)\s+(-\w+|==|>=|<=|<|>|!=|=)\s+(\$?\w+)\s*)/) {
			print "$control ";

			#goes thru each component of expression to print valid output
			while($type =~ /([^\s]+)/gi) {
				$val = $1;
				if ($val=~ /\$([a-zA-Z]+([_\w+]*))/) {
					print "$1";
				}
				elsif ($val =~ /(-\w+|==|>=|<=|<|>|!=|=)/) {
					$op = getOp($1);
					print " $op ";
				}
				elsif ($val =~ /\s*([a-zA-Z]+)\s*/) {
					print "'$1'";
				}
				elsif ($val =~ /\s*\$([0-9]+)\s*/) {
					#assume arg is an int if not != operator
					if ($op !~ /!=/) { print "int(sys.argv[$1])"}
					else { print "sys.argv[$1]"}
				}
				elsif ($val =~ /\s*([0-9]+)\s*/) {
					print "$1";
				}
			}
		}
		print ": $comment\n";
		
		#control is if/elif, skips the next 'then' command
		if ($control =~ /(^\s*(el)?if)/) { 
			$line = shift @input;
			chomp $line;
			#control is if adds to ifFlag for indentation
			if ($control =~ /^\s*if/) { $ifFlag += 1 }
		}
	}
	
	###############################################
	#DONE ELSE FImatches other/ending statements (else/done/fi)
	###############################################
	elsif ($line =~ /^\s*(done|else|fi)\s*/) {
		$cmd = $1;
		if ($cmd =~ /done/ && $loopFlag > 0) {$loopFlag -= 1}
		elsif ($cmd =~ /else/) { 
			printTab(-1);	
			print "else:\n" 
		}
		elsif ($cmd =~ /fi/) { $ifFlag -= 1; }	
	}	
	# Lines we can't translate are turned into comments
	else {
		print "#$line\n";
	}
}

####################
# Helper Functions #
####################

# Searches thru all code and determine what to import
sub getFlags {
	my (@input) = @_;
	foreach my $line (@input) {
		if ($line =~ /(^\s*exit\s+\w+|^\s*read\s+\w+|\$[0-9@!_])/) {
			$sysFlag = 1;
		}		
		if ($line =~ /(\s*\b$shellKeywords\b\s*)/) {
			$subFlag = 1;
		}
		if ($line =~ /(^\s*\bcd\s*( \w+)?)|(\s+(test|\[|\[\[)\s+-\w\s+)/) {
			$osFlag = 1;
		}		
		if ($line =~ /(in\s*\*\.\w+)/) {
			$globFlag = 1;
		}
	}
}

# Print tabs for indentation based on loop/if depth
sub printTab {
	my ($mod) = @_;
	for(my $i = 0; $i < $loopFlag+$ifFlag+$mod; $i++) { print "\t" }
}

#helper function to set comment to "" if undefined
sub commentDefine {
	my ($comment) = @_;
	if (!defined $comment) { return ""}
	return $comment;
}

#helper function to return symbols for options
sub getOp {
	my ($op) = @_;
	if ($op =~ /^\s*(-eq|=)\s*$/) {return "=="}
	elsif ($op =~ /^\s*(-ne)\s*$/) {return "!="}
	elsif ($op =~ /^\s*(-gt)\s*$/) {return ">"}
	elsif ($op =~ /^\s*(-ge)\s*$/) {return ">="}
	elsif ($op =~ /^\s*(-lt)\s*$/) {return "<"}
	elsif ($op =~ /^\s*(-le)\s*$/) {return "<="}
	else { return $op }
}
