#include <stdio.h>
#include <math.h> 

int inTriangle (double x0,double y0, double x1, double y1, double x2, double y2, double x3, double y3);

int main(void){
	int c, r, i, j;
	double xmin, ymin, xmax, ymax,x0,y0,x1, y1, x2, y2, x3, y3;
	scanf("%d%d%lf%lf%lf%lf%lf%lf%lf%lf%lf%lf",&r,&c,&xmin,&ymin,&xmax,&ymax,&x1,&y1,&x2,&y2,&x3,&y3);
	
	printf("+");
	for(i=1; i<=c; i++){
		printf ("-");
	}
	printf ("+\n");
 
	for (j=0; j<r; j++){ 
		printf("|");
		for(i=0; i<c; i++){
			x0=(((xmax-xmin)/(c-1))*i)+xmin;
			y0=(((ymax-ymin)/(r-1))*(r-j-1))+ymin;

			if (inTriangle(x0,y0,x1,y1,x2,y2,x3,y3)){ 
				printf("*");
			}
			else {
				printf(" ");
			}    
		}
		printf("|\n");
	}	
  
	printf("+");
	for(i=1; i<=c; i++){
		printf ("-");
	}
	printf ("+\n");
	 
	return 0;
}

int inTriangle(double x0,double y0, double x1, double y1, double x2, double y2, double x3, double y3) {

	double tri1,tri2,tri3,triM;

	tri1=fabs(((x0*y2)+(x2*y3)+(x3*y0)-(x0*y3)-(x2*y0)-(x3*y2))/2);
	tri2=fabs(((x1*y0)+(x0*y3)+(x3*y1)-(x1*y3)-(x0*y1)-(x3*y0))/2);
	tri3=fabs(((x1*y2)+(x2*y0)+(x0*y1)-(x1*y0)-(x2*y1)-(x0*y2))/2);
	triM=fabs(((x1*y2)+(x2*y3)+(x3*y1)-(x1*y3)-(x2*y1)-(x3*y2))/2);

	if (fabs((tri1+tri2+tri3)-triM)<0.000001){
		return 1;
	} 
	else {
		return 0;
	}
}
