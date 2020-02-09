#include <iostream>
#include <cmath>
using namespace std;

double a, b, c; 		//коэффициенты уравнения
double epsilon; 		//точность решения
int count_of_roots;		//количество корней
double step;			//шаг 

double calculate_function_value(double argument);
double binary_method(double begin_of_segment, double end_of_segment);
double halfinterval(double starting_point, double step);

int main()
{
	//ввод данных
	cout << "введите точность: ";
	cin >> epsilon;

	cout << "введите коэффициенты кубического уравнения" << endl;
	cout << "a = "; cin >> a;
	cout << "b = "; cin >> b;
	cout << "c = "; cin >> c;

	cout << "введите шаг" << endl;
	cin >> step;

	//считаем дискриминант производной a^2 - 3b
	double discriminant;
	discriminant = pow(a, 2) - 3 * b;
	cout << "discriminant = " << discriminant << endl;

	//случай 1: дискриминант < епсилон
	if (discriminant < epsilon)
	{
		count_of_roots = 1;
		double root = halfinterval(0, 0.1);
		//вывод решения
		cout << "количество корней уравнения: " << count_of_roots << endl;
		cout << "корень х1 = " << root << endl;
		return 0;
	}

	//случай 2: дискриминант > епсилон
	double L, B;						//корни f'(x)
	double value_from_L, value_from_B;  //значение функции в корнях f'(x)
	double root_1, root_2;
	if (discriminant > epsilon)
	{
		L = (-a - sqrt(discriminant)) / 3;
		B = (-a + sqrt(discriminant)) / 3;
		value_from_L = calculate_function_value(L);
		value_from_B = calculate_function_value(B);

		//отладка
		cout << "L = " << L << endl;
		cout << "B = " << B << endl;
		cout << "value_from_L = " << value_from_L << endl;
		cout << "value_from_B = " << value_from_B << endl;

		//случай 2.1: f(L) > E, f(B) > E
		if (value_from_L > epsilon && value_from_B > epsilon)
		{
			count_of_roots = 1;
			root_1 = halfinterval(L, -step);
			//вывод решения
			cout << "количество корней уравнения: " << count_of_roots << endl;
			cout << "корень х1 = " << root_1 << endl;
			return 0;
		}
		/*
		//случай 2.2: f(L) > E, f(B) > E
		if (value_from_L > epsilon && value_from_B > epsilon)
		{
			halfinterval(L, -step);
		}*/
	}



	return 0;
}

//возращает значение функции f(x)
double calculate_function_value(double argument)
{
	return pow(argument, 3) + a * pow(argument, 2) + b * argument + c;
}

//отладка
int i = 0;

//метод половинного отрезка
double binary_method(double begin_of_segment, double end_of_segment)
{
	double half_of_segment = (begin_of_segment + end_of_segment) / 2;
	double value_of_function = calculate_function_value(half_of_segment);

	//отладка
	cout << "i was going in binary_method" << endl;

	if (abs(value_of_function) < epsilon)	//нашла корень
	{

		//отладка
		cout << "i'm here 3" << endl;

		return half_of_segment;
	}
	else if (value_of_function > epsilon)
	{

		//отладка
		cout << "i was going in value_of_function > epsilon" << endl; 
		cout << "begin_of_segment = " << begin_of_segment << "half_of_segment = " << half_of_segment << endl << endl;

		return binary_method(begin_of_segment, half_of_segment);
	}
	else if (value_of_function < -epsilon)
	{
		//отладка
		cout << "i'm here 5" << i << endl;
		i++;

		return binary_method(half_of_segment, end_of_segment);
	}
}

//находит корень на полуинтервале
double halfinterval(double starting_point, double step)
{
	//отладка
	cout << "i was going in halfinterval" << endl;

	double root;
	double value_of_function = calculate_function_value(starting_point + step);

	if (value_of_function < 0)
	{
		return halfinterval(starting_point + step, step);
	}
	else if (value_of_function == 0)
	{
		root = starting_point + step;
	}
	else
	{
		root = binary_method(starting_point, starting_point + step);
	}
	return root;
}
