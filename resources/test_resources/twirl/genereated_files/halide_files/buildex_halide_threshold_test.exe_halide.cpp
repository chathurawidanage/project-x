#include <Halide.h>
  #include <vector>
  using namespace std;
  using namespace Halide;
  int main(){ 

Var x_0;
Var x_1;
Var x_2;
Var x_3;
Func output_1;
Expr output_1_p__1 = -1 ;
Expr output_1_p__0 = select(true, (  ( 0  ) & 255 ) ,output_1_p__1);
output_1(x_0,x_1) = cast<uint8_t>( clamp(output_1_p__0,0,255) );

vector<Argument> arguments;
output_1.compile_to_file("halide_out_0",arguments);
return 0;
}
