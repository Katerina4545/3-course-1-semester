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
double negative_value(double current_point, double my_step);
double binary_method_for_down(double begin_of_segment, double end_of_segment);

int main()
{
	//ввод данных
	cout << "enter the epsilon: ";
	cin >> epsilon;

	cout << "enter the coefficients" << endl;
	cout << "a = "; cin >> a;
	cout << "b = "; cin >> b;
	cout << "c = "; cin >> c;

	cout << "enter the step" << endl;
	cin >> step;

	//считаем дискриминант производной a^2 - 3b
	double discriminant;
	discriminant = pow(a, 2) - 3 * b;
	cout << "discriminant = " << discriminant << endl;

	//случай 1: дискриминант < епсилон
	if (discriminant < epsilon)
	{
		count_of_roots = 1;
		double root = halfinterval(0, step);
		//вывод решения
		cout << "count roots: " << count_of_roots << endl;
		cout << "root x1 = " << root << endl;
		return 0;
	}

	//случай 2: дискриминант > епсилон
	double L, B;						//корни f'(x)
	double value_from_L, value_from_B;  //значение функции в корнях f'(x)
	double root_1, root_2, root_3;
	double starting_point;
	if (discriminant > epsilon)
	{
		L = (-a - sqrt(discriminant)) / 3;
		B = (-a + sqrt(discriminant)) / 3;
		value_from_L = calculate_function_value(L);
		value_from_B = calculate_function_value(B);

		//случай 2.1: f(L) > E, f(B) > E
		if (value_from_L > epsilon && value_from_B > epsilon)
		{
			count_of_roots = 1;
			starting_point = negative_value(L, 0.5);
			root_1 = halfinterval(starting_point, step);
			//вывод решения
			cout << "count roots: " << count_of_roots << endl;
			cout << "root x1 = " << root_1 << endl;
			return 0;
		}
        //случай 2.2: f(L) < -E, f(B) < -E
        if (value_from_L < -epsilon && value_from_B < -epsilon)
        {
            count_of_roots = 1;
            starting_point = negative_value(B, 0.5);
            root_1 = halfinterval(starting_point, step);
            //вывод решения
            cout << "count roots: " << count_of_roots << endl;
            cout << "root x1 = " << root_1 << endl;
            return 0;
        }
        //случай 2.3: f(L) > E, f(B) < -E
        /*DEBAG*/
        printf("%d\n%d\n", value_from_L, value_from_B);

        if (value_from_L > epsilon && value_from_B < -epsilon)
        {
            count_of_roots = 3;
            starting_point = negative_value(L, 0.5);
            root_1 = halfinterval(starting_point, step);

            root_2 = binary_method_for_down(L, B);

            root_3 = halfinterval(B, step);

            //вывод решения
            cout << "count roots: " << count_of_roots << endl;
            cout << "root x1 = " << root_1 << endl;
            cout << "root x2 = " << root_2 << endl;
            cout << "root x3 = " << root_3 << endl;
            return 0;
        }
        //случай 2.4: f(L) > E, |f(B)| < E
        if (value_from_L > epsilon && abs(value_from_B) < epsilon)
        {
            count_of_roots = 2;
            starting_point = negative_value(L, 0.5);
            root_1 = halfinterval(starting_point, step);
            root_2 = B;

            //вывод решения
            cout << "count roots: " << count_of_roots << endl;
            cout << "root x1 = " << root_1 << endl;
            cout << "root x2 = " << root_2 << endl;
            return 0;
        }
        //случай 2.5: |f(L)| < E, f(B) < -E
        if (value_from_L > epsilon && abs(value_from_B) < epsilon)
        {
            count_of_roots = 2;
            starting_point = negative_value(L, 0.5);
            root_1 = L;
            root_2 = halfinterval(B, step);

            //вывод решения
            cout << "count roots: " << count_of_roots << endl;
            cout << "root x1 = " << root_1 << endl;
            cout << "root x2 = " << root_2 << endl;
            return 0;
        }
        //случай 2.6: |f(L)| < E, |f(B)| < E
        if (value_from_L > epsilon && abs(value_from_B) < epsilon)
        {
            count_of_roots = 1;
            root_1 = (L + B) / 2;
            //вывод решения
            cout << "count roots: " << count_of_roots << endl;
            cout << "root x1 = " << root_1 << endl;
            return 0;
        }
	}
}

//возращает значение функции f(x)
double calculate_function_value(double argument)
{
	return pow(argument, 3) + a * pow(argument, 2) + b * argument + c;
}

//метод половинного отрезка
double binary_method(double begin_of_segment, double end_of_segment)
{
	double half_of_segment = (begin_of_segment + end_of_segment) / 2;
	double value_of_function = calculate_function_value(half_of_segment);

	if (abs(value_of_function) < epsilon)	//нашла корень
	{
		return half_of_segment;
	}
	else if (value_of_function > epsilon)
	{
		return binary_method(begin_of_segment, half_of_segment);
	}
	else if (value_of_function < -epsilon)
	{
		return binary_method(half_of_segment, end_of_segment);
	}
}

double binary_method_for_down(double begin_of_segment, double end_of_segment) {
    double half_of_segment = (begin_of_segment + end_of_segment) / 2;
    double value_of_function = calculate_function_value(half_of_segment);

    if (abs(value_of_function) < epsilon)	//нашла корень
    {
        return half_of_segment;
    }
    else if (value_of_function > epsilon)
    {
        return binary_method_for_down(half_of_segment, end_of_segment);
    }
    else if (value_of_function < -epsilon)
    {
        return binary_method_for_down(begin_of_segment, half_of_segment);
    }
}

//находим точку, в которой значение функции < 0 , откуда будем стартовать halfinterval()
double negative_value(double current_point, double my_step)
{
    double value_of_function = calculate_function_value(current_point);
    if(value_of_function > 0)
    {
        return negative_value(current_point - my_step, my_step);
    }
    else
    {
        return current_point;
    }
}

//находит отрезок, где лежит корень на полуинтервале
double halfinterval(double starting_point, double step)
{
	double root;
	double value_of_function = calculate_function_value(starting_point + step);

	if (value_of_function < 0)
	{
		return halfinterval(starting_point + step, step);
	}
	else if (value_of_function > 0)
	{
        root = binary_method(starting_point, starting_point + step);
	}
	else
	{
        root = starting_point + step;
	}
	return root;
}
