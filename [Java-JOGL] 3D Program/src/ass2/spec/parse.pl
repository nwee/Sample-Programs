#!/usr/bin/perl -w
@vertices = ();
@normals = ();
@texture = ();

$vertices = "";
$normals = "";
$texture = "";
foreach $line (<stdin>) {
	if ($line =~ /^v /) {
		push @vertices, $line;
	} elsif ($line =~ /^vn /) {
		push @normals, $line;
	} elsif ($line =~ /^vt /) {
		push @texture, $line;
	}	
	#parse vertice indexes
	next if $line !~ /^f /;
	$line =~ /^f (\d+)\/(\d+)\/(\d+) (\d+)\/(\d+)\/(\d+) (\d+)\/(\d+)\/(\d+)/;
	@temp = split (" ", $vertices[$1-1]);
	$vertices .= "$temp[1]\n$temp[2]\n$temp[3]\n";
	@temp = split (" ", $vertices[$4-1]);
	$vertices .= "$temp[1]\n$temp[2]\n$temp[3]\n";
	@temp = split (" ", $vertices[$7-1]);
	$vertices .= "$temp[1]\n$temp[2]\n$temp[3]\n";

	#parse vertice normals
	@temp = split (" ", $normals[$3-1]);
	$normals.= "$temp[1]\n$temp[2]\n$temp[3]\n";
	@temp = split (" ", $normals[$6-1]);
	$normals .= "$temp[1]\n$temp[2]\n$temp[3]\n";
	@temp = split (" ", $normals[$9-1]);
	$normals .= "$temp[1]\n$temp[2]\n$temp[3]\n";

	#parse vertice texture coords
	@temp = split (" ", $texture[$2-1]);
	$texture .= "$temp[1]\n$temp[2]\n";
	@temp = split (" ", $texture[$5-1]);
	$texture .= "$temp[1]\n$temp[2]\n";
	@temp = split (" ", $texture[$8-1]);
	$texture .= "$temp[1]\n$temp[2]\n";
}

open(my $vf, '>', 'legoManVertices.txt') or die "Could not open file";
print $vf $vertices;
close $vf;
open(my $vnf, '>', 'legoManNormals.txt') or die "Could not open file";
print $vnf $normals;
close $vnf;
open(my $vtf, '>', 'legoManTexture.txt') or die "Could not open file";
print $vtf $texture;
close $vtf;
